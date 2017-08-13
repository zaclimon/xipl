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

import android.content.Context;
import android.media.tv.TvInputManager;
import android.media.tv.TvTrackInfo;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.UnrecognizedInputFormatException;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.media.tv.companionlibrary.BaseTvInputService;
import com.google.android.media.tv.companionlibrary.TvPlayer;
import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.model.RecordedProgram;
import com.zaclimon.xipl.R;
import com.zaclimon.xipl.player.ProviderTvPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom {@link BaseTvInputService} used for a given provider.
 *
 * @author zaclimon
 * Creation date: 11/06/17
 */

public class ProviderTvInputService extends BaseTvInputService {

    @Override
    public final Session onCreateSession(String inputId) {
        ProviderSession providerSession = new ProviderSession(this, inputId);
        return (super.sessionCreated(providerSession));
    }

    /**
     * Custom {@link com.google.android.media.tv.companionlibrary.BaseTvInputService.Session} which
     * handles playback when a {@link Channel} is tuned.
     * <p>
     * It also implements an {@link Player.EventListener} in which
     * it can adapt better to callbacks from a {@link ProviderTvPlayer}
     */
    private class ProviderSession extends Session implements Player.EventListener {

        private ProviderTvPlayer mProviderTvPlayer;
        private Context mContext;
        private static final boolean DEBUG = false;

        /**
         * Base constructor
         *
         * @param context context which will be used for session.
         * @param inputId the input id of the application
         */
        public ProviderSession(Context context, String inputId) {
            super(context, inputId);
            mContext = context;
        }

        @Override
        public boolean onPlayProgram(Program program, long startPosMs) {
            return (true);
        }

        @Override
        public void onPlayChannel(Channel channel) {
            mProviderTvPlayer = new ProviderTvPlayer(mContext, channel.getInternalProviderData().getVideoUrl());
            mProviderTvPlayer.addListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_UNSUPPORTED);
            }

            if (DEBUG) {
                Log.d(getClass().getSimpleName(), "Video Url: " + channel.getInternalProviderData().getVideoUrl());
                Log.d(getClass().getSimpleName(), "Video format: " + channel.getVideoFormat());
            }

            // Notify when the video is available so the channel surface can be shown to the screen.
            mProviderTvPlayer.play();
        }

        @Override
        public void onSetCaptionEnabled(boolean enabled) {
        }

        @Override
        public boolean onPlayRecordedProgram(RecordedProgram recordedProgram) {
            return (false);
        }

        @Override
        public TvPlayer getTvPlayer() {
            return (mProviderTvPlayer);
        }

        @Override
        public boolean onTune(Uri channelUri) {

            // Notify the video to be unavailable in order to not show artifacts when changing channels.
            notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_TUNING);
            releasePlayer();
            return super.onTune(channelUri);
        }

        @Override
        public void onRelease() {
            super.onRelease();
            releasePlayer();
        }

        /**
         * Method that stops and releases a given player's resources.
         */
        public void releasePlayer() {
            if (mProviderTvPlayer != null) {
                mProviderTvPlayer.setSurface(null);
                mProviderTvPlayer.stop();
                mProviderTvPlayer.release();
                mProviderTvPlayer = null;
            }
        }

        /**
         * Gets a list of given tracks for a given channel, whether it is video or audio.
         * <p>
         * This way, it is possible for a user to see in the Live Channels application the video
         * resolution and the audio layout.
         *
         * @return the track list usable by the Live Channels application
         */
        private List<TvTrackInfo> getAllTracks() {
            List<TvTrackInfo> tracks = new ArrayList<>();
            tracks.add(getTrack(TvTrackInfo.TYPE_VIDEO));
            tracks.add(getTrack(TvTrackInfo.TYPE_AUDIO));
            return (tracks);
        }

        /**
         * Returns a given track based on the player's video or audio format.
         *
         * @param trackType the type as defined by {@link TvTrackInfo#TYPE_VIDEO} or {@link TvTrackInfo#TYPE_AUDIO}
         * @return the related {@link TvTrackInfo} for a given player track.
         */
        private TvTrackInfo getTrack(int trackType) {

             /*
              Note that we should allow for multiple tracks. However, since this is a TV stream, it
              most likely consists of one video, one audio and one subtitle track.

              We're skipping the subtitle track since it gets mostly delayed and might not offer
              the best experience. It might get added in the future.
              */

            Format format = null;
            TvTrackInfo.Builder builder;

            // Versions before Nougat expects a general TvTrackInfo id and not one based on the type.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder = new TvTrackInfo.Builder(trackType, Integer.toString(0));
            } else {
                builder = new TvTrackInfo.Builder(trackType, Integer.toString(trackType));
            }

            if (trackType == TvTrackInfo.TYPE_VIDEO) {
                format = mProviderTvPlayer.getVideoFormat();
            } else if (trackType == TvTrackInfo.TYPE_AUDIO) {
                format = mProviderTvPlayer.getAudioFormat();
            }

            if (format != null) {
                if (trackType == TvTrackInfo.TYPE_VIDEO) {
                    if (format.width != Format.NO_VALUE) {
                        builder.setVideoWidth(format.width);
                    }

                    if (format.height != Format.NO_VALUE) {
                        builder.setVideoHeight(format.height);
                    }
                } else {
                    builder.setAudioChannelCount(format.channelCount);
                    builder.setAudioSampleRate(format.sampleRate);
                }
            }
            return (builder.build());
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            if (playWhenReady && playbackState == Player.STATE_READY) {
                notifyTracksChanged(getAllTracks());
                notifyTrackSelected(TvTrackInfo.TYPE_VIDEO, getTrackId(TvTrackInfo.TYPE_VIDEO));
                notifyTrackSelected(TvTrackInfo.TYPE_AUDIO, getTrackId(TvTrackInfo.TYPE_AUDIO));
                notifyVideoAvailable();
            }

            if (DEBUG) {
                Log.d(getClass().getSimpleName(), "Player state changed to " + playbackState + ", PWR: " + playWhenReady);
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            if (error.getCause() instanceof BehindLiveWindowException) {
                Toast.makeText(mContext, R.string.stream_failure_retry, Toast.LENGTH_SHORT).show();
                mProviderTvPlayer.restart(mContext);
                mProviderTvPlayer.play();
            } else if (error.getCause() instanceof UnrecognizedInputFormatException) {
                // Channel cannot be played in case of an error in parsing the ".m3u8" file.
                Toast.makeText(mContext, mContext.getString(R.string.channel_stream_failure), Toast.LENGTH_SHORT).show();
            } else if (error.getCause() instanceof HttpDataSource.InvalidResponseCodeException) {

                 /*
                  We might get errors different like 403 which indicate permission denied due to multiple
                  connections at the same time or 502 meaning bad gateway.

                  Restart the loading after 5 seconds.
                  */

                SystemClock.sleep(5000);
                Toast.makeText(mContext, R.string.stream_failure_retry, Toast.LENGTH_SHORT).show();
                mProviderTvPlayer.restart(mContext);
                mProviderTvPlayer.play();
            } else if (error.getCause() instanceof HttpDataSource.HttpDataSourceException) {
                // Timeout, nothing we can do really...
                Toast.makeText(mContext, R.string.channel_stream_failure, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPositionDiscontinuity() {
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
        }

        /**
         * Gives the id for a given track based on it's type between {@link TvTrackInfo#TYPE_VIDEO}
         * or {@link TvTrackInfo#TYPE_AUDIO}
         *
         * @param trackType The track type of the stream
         * @return the default track id used for the stream.
         */
        private String getTrackId(int trackType) {

            /*
             Android versions before Nougat, each track has a global unique id that can't be used
             for different track types.
             */
            if ((trackType == TvTrackInfo.TYPE_VIDEO || trackType == TvTrackInfo.TYPE_AUDIO) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return (Integer.toString(0));
            } else {
                return (Integer.toString(trackType));
            }
        }

    }
}