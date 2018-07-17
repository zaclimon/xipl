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

package com.zaclimon.xipl.service;

import android.app.job.JobParameters;
import android.content.Intent;
import android.net.Uri;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.sync.EpgSyncJobService;
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser;
import com.zaclimon.xipl.Constants;
import com.zaclimon.xipl.properties.ChannelProperties;
import com.zaclimon.xipl.util.ProviderChannelUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Custom {@link EpgSyncJobService} used for syncing a given IPTV provider content.
 * <p>
 * It downloads and parses the content from a user's M3U playlist + possible EPG XMLTV to adds it to
 * the Android TV's system database in order to be used by the Live Channels application.
 *
 * @author zaclimon
 * Creation date: 11/06/17
 */

public abstract class ProviderEpgService extends EpgSyncJobService implements EpgProcessingCallback {

    private final String LOG_TAG = getClass().getSimpleName();

    private List<Channel> mChannels;
    private String mInputId;
    private XmlTvParser.TvListing mTvListing;
    private JobParameters mJobParameters;

    /**
     * Used to get the playlist URL from a provider.
     * @return the M3U playlist url for a given provider
     */
    protected abstract String getPlaylistUrl();

    /**
     * Gets the EPG URL for a given provider (if any)
     * @return the EPG URL for a provider
     */
    protected abstract String getEpgUrl();

    /**
     * Returns the {@link ChannelProperties} for a given provider
     * @return the properties for a provider.
     */
    protected abstract ChannelProperties getChannelProperties();

    @Override
    public List<Channel> getChannels() {
        return (mChannels);
    }

    @Override
    public List<Program> getProgramsForChannel(Uri channelUri, Channel channel, long startMs, long endMs) {

        /*
         The XMLTV file an EPG contains an ID that might be used across one or several channels.
         In that case, it will be stored in the internal provider data of the given channel.

         XmlTvParser has a internal HashMap in which Programs can be retrieved. Let's use it as our
         primary means of getting them. If there aren't any available programs for a channel,
         create a dummy program so the EPG guide from "Live Channels" can categorize the channel.

         Finally, retrieve a given channel's genre based on it's internal provider data. This
         information will be passed to the Live Channels's guide.
         */

        InternalProviderData internalProviderData = channel.getInternalProviderData();

        try {
            if (internalProviderData != null && internalProviderData.has(Constants.EPG_ID_PROVIDER)) {

                // The provider data gets parsed as a string by default, same for the genres.
                List<Program> tempPrograms = new ArrayList<>();
                String epgId = (String) internalProviderData.get(Constants.EPG_ID_PROVIDER);
                String channelGenresJson = (String) internalProviderData.get(Constants.CHANNEL_GENRES_PROVIDER);
                int epgIdInt = Integer.parseInt(epgId);

                if (epgIdInt != 0 && mTvListing != null && mTvListing.getPrograms(epgIdInt) != null) {
                    tempPrograms = mTvListing.getPrograms(epgIdInt);
                }

                // Create a dummy program for listing a channel's genre if there are no programs.
                if (tempPrograms.isEmpty()) {
                    Program.Builder builder = new Program.Builder(channel);
                    long startTimeMillis = ProviderChannelUtil.getLastHalfHourMillis();
                    long endTimeMillis = startTimeMillis + TimeUnit.DAYS.toMillis(7);
                    builder.setStartTimeUtcMillis(startTimeMillis);
                    builder.setEndTimeUtcMillis(endTimeMillis);
                    builder.setCanonicalGenres(ProviderChannelUtil.getGenresArrayFromJson(channelGenresJson));
                    tempPrograms.add(builder.build());
                } else {
                    // Set genres a given channel if it has programs.
                    for (int i = 0; i < tempPrograms.size(); i++) {
                        Program.Builder builder = new Program.Builder(tempPrograms.get(i));
                        builder.setCanonicalGenres(ProviderChannelUtil.getGenresArrayFromJson(channelGenresJson));
                        tempPrograms.set(i, builder.build());
                    }
                }

                return (tempPrograms);
            }
        } catch (InternalProviderData.ParseException ps) {
            Log.e(LOG_TAG, "Channel " + channel.getDisplayName() + " Couldn't get checked");
        }

        return (null);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        // Broadcast status
        mInputId = params.getExtras().getString(BUNDLE_KEY_INPUT_ID);
        mJobParameters = params;

        Intent intent = new Intent(ACTION_SYNC_STATUS_CHANGED);
        intent.putExtra(BUNDLE_KEY_INPUT_ID, mInputId);
        Log.d(LOG_TAG, "Sync program data for " + mInputId);
        intent.putExtra(SYNC_STATUS, SYNC_STARTED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        AsyncEpgProcess asyncEpgProcess = new AsyncEpgProcess(this, getPlaylistUrl(), getEpgUrl(), getChannelProperties(), this);
        asyncEpgProcess.execute();
        return (true);
    }

    @Override
    public void onProcessSuccess(List<Channel> channels, XmlTvParser.TvListing listing) {
        mChannels = channels;
        mTvListing = listing;
        EpgSyncTask epgSyncTask = new EpgSyncTask(mJobParameters);
        epgSyncTask.execute();
    }

    @Override
    public void onProcessFailed() {
        // Cancel the sync if we couldn't retrieve the link anything
        Log.e(LOG_TAG, "Couldn't retrieve the playlist/EPG");
        Intent intent = new Intent(EpgSyncJobService.ACTION_SYNC_STATUS_CHANGED);
        intent.putExtra(EpgSyncJobService.SYNC_STATUS, EpgSyncJobService.SYNC_ERROR);
        intent.putExtra(BUNDLE_KEY_ERROR_REASON, EpgSyncJobService.ERROR_NO_CHANNELS);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}