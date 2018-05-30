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


import android.content.Context;

import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.zaclimon.xipl.Constants;
import com.zaclimon.xipl.R;
import com.zaclimon.xipl.model.AvContent;
import com.zaclimon.xipl.properties.ChannelProperties;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Utility class used for retrieving, handling an IPTV provider stuff.
 *
 * @author zaclimon
 * Creation date: 09/06/17
 */

public class ProviderChannelUtil {

    private static final String LOG_TAG = "ProviderChannelUtil";

    /**
     * Gets a list of channels based on the M3U playlist of a given user.
     *
     * @param playlist List of the playlist lines containing the user's channels
     * @param context  the context required for some other operations (Getting the genre for example)
     * @return the list of channels for a given user
     */
    public static List<Channel> createChannelList(InputStream playlist, Context context, ChannelProperties properties) {

        List<AvContent> channelContents = AvContentUtil.getAvContentsList(playlist);
        List<Channel> tempList = new ArrayList<>();
        int channelNumber = 1;

        /*
         Google is kind of "weird" when it comes to the way it has of tuning channel/program
         data. In most cases, the TIF expects to have a program available for each channels in order
         to play them.

         However, a provider might not have all programs available for a given channel so simply save the EPG
         id and leave the parsing to when programs will get created.
         */

        for (int i = 0; i < channelContents.size(); i++) {
            Channel channel;
            String tempName = channelContents.get(i).getTitle();
            String tempLogo = channelContents.get(i).getLogo();
            String tempLink = channelContents.get(i).getContentLink();
            String tempGroup = channelContents.get(i).getGroup();
            int tempId = channelContents.get(i).getId();

            if (properties.hasChannelLogo()) {
                channel = createChannel(tempName, Integer.toString(i + 1), tempId, tempLogo, tempLink, tempGroup, getProgramGenre(tempName, context));
            } else {
                channel = createChannel(tempName, Integer.toString(i + 1), tempId, null, tempLink, tempGroup, getProgramGenre(tempName, context));
            }

            // Some users might have playlist items that aren't valid channels, remove them.
            if (properties.isLiveChannel(channel) && properties.isChannelRegionValid(channel) && properties.isChannelGenreValid(channel) && properties.isChannelGroupValid(channel)) {
                // Some channels might get filtered so let's use a counter which will only register "valid" ones.
                channel = createChannel(channel, Integer.toString(channelNumber));
                tempList.add(channel);
                channelNumber++;
            }
        }
        return (tempList);
    }

    /**
     * Creates a {@link Channel} that can be used by the Android TV framework and the Live Channels application
     * if a channel has been created before.
     *
     * @param originalChannel the previously available channel
     * @param displayNumber the desired channel number on the Live Channels application
     * @return the channel to be used by the system
     */
    private static Channel createChannel(Channel originalChannel, String displayNumber) {
        Channel.Builder builder = new Channel.Builder(originalChannel);
        builder.setDisplayNumber(displayNumber);
        return (builder.build());
    }

    /**
     * Creates a {@link Channel} that can be used by the Android TV framework and the Live Channels application.
     *
     * @param displayName   the display name of the channel
     * @param displayNumber the display number of the channel
     * @param epgId         the id as defined in {@link com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser}
     * @param logo          the logo url link
     * @param url           the video url link
     * @return the channel to be used by the system.
     */
    private static Channel createChannel(String displayName, String displayNumber, int epgId, String logo, String url, String group, String[] genres) {

        /*
         In order to map correctly the programs to a given channel, store the EPG id somewhere in the
         channel so we can retrieve it when we'll need to find programs

         Using the EPG ID as a good way to have an original network id but it might create channel
         duplicates. Since some channels either don't have an EPG id (which makes 0 as a hash) or might
         share the same id altogether, (same channel in SD/HD for example) they get recreated
         as their original id isn't really original anymore...

         In that case, let's use the display name as the original network id instead of the EPG id.
         Let's also retrieve the an example genre for the channel so it can be passed on the side
         of the EPG guide.
        */

        Channel.Builder builder = new Channel.Builder();
        InternalProviderData internalProviderData = new InternalProviderData();

        try {
            JSONArray genresJsonArray = new JSONArray(genres);
            internalProviderData.put(Constants.EPG_ID_PROVIDER, epgId);
            internalProviderData.put(Constants.CHANNEL_GENRES_PROVIDER, genresJsonArray);
        } catch (InternalProviderData.ParseException ps) {
            // Can't do anything about this...
        } catch (JSONException json) {
            json.printStackTrace();
        }

        internalProviderData.setVideoUrl(url);
        builder.setDisplayName(displayName);
        builder.setDisplayNumber(displayNumber);
        builder.setOriginalNetworkId(displayName.hashCode());
        builder.setChannelLogo(logo);
        builder.setNetworkAffiliation(group);
        builder.setInternalProviderData(internalProviderData);
        return (builder.build());
    }

    /**
     * Gives one or more genre(s) for a given {@link com.google.android.media.tv.companionlibrary.model.Program}
     * based on it's channel. The genre(s) must be one of the following:
     * <p>
     * {@link android.media.tv.TvContract.Programs.Genres#FAMILY_KIDS}
     * {@link android.media.tv.TvContract.Programs.Genres#SPORTS}
     * {@link android.media.tv.TvContract.Programs.Genres#SHOPPING}
     * {@link android.media.tv.TvContract.Programs.Genres#MOVIES}
     * {@link android.media.tv.TvContract.Programs.Genres#COMEDY}
     * {@link android.media.tv.TvContract.Programs.Genres#TRAVEL}
     * {@link android.media.tv.TvContract.Programs.Genres#DRAMA}
     * {@link android.media.tv.TvContract.Programs.Genres#EDUCATION}
     * {@link android.media.tv.TvContract.Programs.Genres#ANIMAL_WILDLIFE}
     * {@link android.media.tv.TvContract.Programs.Genres#NEWS}
     * {@link android.media.tv.TvContract.Programs.Genres#GAMING}
     * {@link android.media.tv.TvContract.Programs.Genres#ARTS}
     * {@link android.media.tv.TvContract.Programs.Genres#ENTERTAINMENT}
     * {@link android.media.tv.TvContract.Programs.Genres#LIFE_STYLE}
     * {@link android.media.tv.TvContract.Programs.Genres#MOVIES}
     * {@link android.media.tv.TvContract.Programs.Genres#MUSIC}
     * {@link android.media.tv.TvContract.Programs.Genres#PREMIER}
     * {@link android.media.tv.TvContract.Programs.Genres#TECH_SCIENCE}
     *
     * @param channelName The name of the channel
     * @param context     context required to access a string containing the channel names
     * @return the genre of the channel as specified by in {@link android.media.tv.TvContract.Programs.Genres}
     */
    private static String[] getProgramGenre(String channelName, Context context) {

         /*
         Another thing that is weird with Google is that a given channel has a genre that shows
         content based on it. However, it is the programs that has them.

         I understand where the decision could come from since a Program could have a genre different
         from it's channel. Nonetheless, it's the only way to do some kind of easy grouping in the
         Live Channels app.
         */

        List<String[]> allChannelGroups = new ArrayList<>();
        List<String> currentChannelGroups = new ArrayList<>();

        // Add all the groups now so we know we won't have any problems when comparing.
        allChannelGroups.add(context.getResources().getStringArray(R.array.animal_wildlife_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.arts_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.comedy_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.drama_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.education_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.entertainment_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.family_kids_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.gaming_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.lifestyle_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.movies_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.music_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.news_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.premier_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.shopping_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.sports_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.tech_science_channels));
        allChannelGroups.add(context.getResources().getStringArray(R.array.travel_channels));

        for (int i = 0; i < allChannelGroups.size(); i++) {
            String[] currentGroup = allChannelGroups.get(i);
            for (String channelType : currentGroup) {
                if (channelName.contains(channelType)) {
                    currentChannelGroups.add(getGenreByPosition(i));
                    break;
                }
            }
        }

        String[] channelGroupsArray = new String[currentChannelGroups.size()];
        channelGroupsArray = currentChannelGroups.toArray(channelGroupsArray);
        return (channelGroupsArray);
    }

    /**
     * Calculates the current date with the last half hour passed.
     *
     * @return the current date with the last half hour in milliseconds
     */
    public static long getLastHalfHourMillis() {
        return (getLastHalfHourMillis(System.currentTimeMillis()));
    }

    /**
     * Calculates the current date with the last half hour passed
     *
     * @param originalMillis the desired calculated time in milliseconds
     * @return the desired date with the last half hour in milliseconds
     */
    public static long getLastHalfHourMillis(long originalMillis) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(originalMillis);

        int minutes = calendar.get(Calendar.MINUTE);
        int difference = minutes % 30;

        if (difference != 0) {
            minutes -= difference;
        }

        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return (calendar.getTimeInMillis());
    }

    /**
     * Gives a Program's genre based on the {@link Constants#CHANNEL_GENRES} array position. If
     * comparing must be made, the order should be based on this particular array as well.
     *
     * @param position the position of the wished genre.
     * @return the genre literal as defined in {@link android.media.tv.TvContract.Programs.Genres}.
     */
    private static String getGenreByPosition(int position) {
        return (Constants.CHANNEL_GENRES[position]);
    }

    /**
     * Gives all possible genres for a given channel based on it's json contents.
     *
     * @param json the json parsed as a String.
     * @return the array of all the genres for the channel.
     */
    public static String[] getGenresArrayFromJson(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            String[] genresArray = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                genresArray[i] = jsonArray.getString(i);
            }
            return (genresArray);
        } catch (JSONException js) {
            js.printStackTrace();
        }
        return (null);
    }

}