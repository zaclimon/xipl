/*
 * Copyright 2017 Isaac Pateau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaclimon.xipl.util;

import android.text.TextUtils;
import android.util.Log;

import com.zaclimon.xipl.Constants;
import com.zaclimon.xipl.model.AvContent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class responsible for handling {@link AvContent}
 * <p>
 * AvContents can be handled differently depending on the provider implementation.
 * For most encountered cases, an M3U playlist was of this form.
 * <p>
 * #EXTINF:-1 tvg-name="" group-title="" tvg-logo="", "title"
 * "content-link"
 *
 * @author zaclimon
 * Creation date: 01/07/17
 */

public class AvContentUtil {

    private static final String LOG_TAG = "AvContentUtil";

    /**
     * Generates required {@link AvContent} for a given M3U playlist
     *
     * @param playlist stream containing the M3U playlist
     * @return the list of AvContents from that playlist
     */
    public static List<AvContent> getAvContentsList(InputStream playlist) {
        return (getAvContentsList(playlist, null));
    }

    /**
     * Generates required {@link AvContent} for a given M3U playlist
     *
     * @param playlist stream containing the M3U playlist
     * @param contentCategory the category in which the content will be
     * @return the list of AvContents from that playlist
     */
    public static List<AvContent> getAvContentsList(InputStream playlist, String contentCategory) {

        List<String> playlistStrings = getAvContentsAsString(playlist);
        List<AvContent> avContents = new ArrayList<>();

        if (playlistStrings != null) {
            int size = playlistStrings.size();

            for (int i = 0; i < size; i++) {
                if (playlistStrings.get(i).contains("#EXTINF")) {
                    // The next line is guaranteed to be the content link.
                    AvContent avContent = createAvContent(playlistStrings.get(i), playlistStrings.get(i + 1), contentCategory);
                    if (avContent != null) {
                        avContents.add(avContent);
                    }
                }
            }
        }

        return (avContents);
    }

    /**
     * Generates the groups for given {@link AvContent}
     *
     * @param contents the list containing all the AvContents
     * @return the list of different groups for the given content
     */
    public static List<String> getAvContentsGroup(List<AvContent> contents) {

        Set<String> tempGroups = new HashSet<>();

        /*
         Some values might not be sorted equally and as a result, there might be duplicates in
         the list. Use HashSet to not include any of them.
         */

        for (int i = 0; i < contents.size(); i++) {
            tempGroups.add(contents.get(i).getGroup());
        }
        return (new ArrayList<>(tempGroups));
    }

    /**
     * Reads a stream from the M3U playlist from a user for easier parsing.
     *
     * @param playlist a user's M3U playlist stream
     * @return a List containing every lines of the M3U playlist
     */
    private static List<String> getAvContentsAsString(InputStream playlist) {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(playlist));
        List<String> contents = new ArrayList<>();
        String tempLine;

        try {
            while ((tempLine = bufferedReader.readLine()) != null) {
                contents.add(tempLine);
            }

            if (contents.size() == 1) {
                return (generatePlaylistLinesSingle(contents.get(0)));
            } else {
                return (generatePlaylistLinesMulti(contents));
            }
        } catch (IOException io) {
            // Couldn't read the stream
            return (null);
        }
    }

    /**
     * Generates the required M3U playlist lines if the given playlist was on a single line (That is
     * without a new line)
     *
     * @param playlist the whole playlist.
     * @return A list containing all the lines of the M3U playlist.
     */
    private static List<String> generatePlaylistLinesSingle(String playlist) {

        /*
         For some API's there might not be any newlines between actual lines
         For this reason, check if there is a whitespace instead of a newline
         before the content link section.

         This way, we can see if it's the real link and not a letter from a content
         title for example.
        */

        List<String> contents = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        boolean firstCharacterRead = false;

        for (int i = 0; i < playlist.length(); i++) {
            char character = playlist.charAt(i);

            if (character == '#' && firstCharacterRead) {
                String currentString = stringBuilder.toString();
                int contentUrlIndex = currentString.lastIndexOf("http://");

                if (contentUrlIndex != -1 && Character.isWhitespace(currentString.charAt(contentUrlIndex - 1))) {
                    String contentUrl = stringBuilder.toString().substring(contentUrlIndex);
                    contents.add(contentUrl.trim());
                    stringBuilder.delete(contentUrlIndex, stringBuilder.length() - 1);
                    contents.add(stringBuilder.toString().trim());
                    stringBuilder = new StringBuilder();
                }
            } else {
                firstCharacterRead = true;
            }
            stringBuilder.append(character);
        }
        return (contents);
    }

    /**
     * Generates all the required lines from a given playlist if that playlist had multiple lines.
     *
     * @param playlist the playlist as a list separated by it's lines
     * @return a list having the required M3U playlist lines.
     */
    private static List<String> generatePlaylistLinesMulti(List<String> playlist) {

        List<String> contents = new ArrayList<>();

        for (String playlistLine : playlist) {
            if (playlistLine.startsWith("#EXTINF") || playlistLine.startsWith("http://")) {
                contents.add(playlistLine);
            }
        }
        return (contents);
    }

    /**
     * Creates a {@link AvContent} from a given playlist line.
     *
     * @param playlistLine The required playlist line
     * @return the AvContent from this line
     */
    private static AvContent createAvContent(String playlistLine, String contentLink, String contentCategory) {

        if (Constants.ATTRIBUTE_TVG_NAME_PATTERN.matcher(playlistLine).find()) {
            String title = getAttributeFromPlaylistLine(Constants.ATTRIBUTE_TVG_NAME_PATTERN, playlistLine);
            String logo = getAttributeFromPlaylistLine(Constants.ATTRIBUTE_TVG_LOGO_PATTERN, playlistLine);
            String group = getAttributeFromPlaylistLine(Constants.ATTRIBUTE_GROUP_TITLE_PATTERN, playlistLine);
            int id = getAttributeFromPlaylistLine(Constants.ATTRIBUTE_TVG_ID_PATTERN, playlistLine).hashCode();

            return (new AvContent(title, logo, group, contentCategory, contentLink, id));
        } else {
            Log.e(LOG_TAG, "Current line not valid for creating AvContent: " + playlistLine);
            return (null);
        }

    }

    /**
     * Returns a given attribute for an M3U playlist file based on it's parameter.
     *
     * @param attribute    an attribute as found in {@link Constants}
     * @param playlistLine a line from a given playlist.
     * @return The attribute for that line.
     */
    private static String getAttributeFromPlaylistLine(Pattern attribute, String playlistLine) {

        Matcher matcher = attribute.matcher(playlistLine);

        if (matcher.find()) {
            String[] parts = matcher.group().split("=");
            String contents = parts[1].replace("\"", "");

             /*
              It might be possible that the title isn't in the tvg-name tag, retrieve it from the
              region after the comma.
              */

            if (TextUtils.isEmpty(contents) && attribute.equals(Constants.ATTRIBUTE_TVG_NAME_PATTERN)) {
                Pattern commaPattern = Pattern.compile(",.*");
                Matcher commaMatcher = commaPattern.matcher(playlistLine);

                if (commaMatcher.find()) {
                    // Don't include the first comma in the title
                    return (commaMatcher.group().substring(1));
                }
            }
            return (contents);
        } else {
            return ("");
        }
    }
}