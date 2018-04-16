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

package com.zaclimon.xipl.player;

import android.content.Context;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.media.tv.companionlibrary.TvPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete simple implementation of the {@link TvPlayer} interface.
 * <p>
 * This current implementation is based on ExoPlayer 2.x series.
 *
 * @author zaclimon
 * Creation date: 11/06/17
 */

public class ProviderTvPlayer implements TvPlayer {

    private SimpleExoPlayer player;
    private List<TvPlayer.Callback> callbacks;
    private String streamUrl;

    /**
     * Main constructor of the player.
     *
     * @param context the context used to initialize the player
     * @param url     the url containing the required stream to be played
     */
    public ProviderTvPlayer(Context context, String url) {
        callbacks = new ArrayList<>();
        streamUrl = url;
        init(context);
    }

    /**
     * Initializes and prepares the player but doesn't make it play the content.
     *
     * @param context the context needed to initialize the player.
     */
    private void init(Context context) {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        player.prepare(getMediaSource(context));
    }

    /**
     * Gets the required {@link MediaSource} depending on the stream source.
     *
     * @param context The required context to get the correct MediaSource
     * @return the MediaSource used for the playback.
     */
    private MediaSource getMediaSource(Context context) {

        Uri mediaUrl = Uri.parse(streamUrl);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getApplicationInfo().loadLabel(context.getPackageManager()).toString()));
        MediaSource mediaSource;

         /*
          Use only a HlsMediaSource if we're sure that we're on a HLS stream. Any other one should
          be an ExtractorMediaSource.
         */

        if (streamUrl.endsWith(".m3u8")) {
            HlsMediaSource.Factory factory = new HlsMediaSource.Factory(dataSourceFactory);
            factory.setAllowChunklessPreparation(true);
            mediaSource = factory.createMediaSource(mediaUrl);
        } else {
            ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(dataSourceFactory);
            mediaSource = factory.createMediaSource(mediaUrl);
        }

        return (mediaSource);
    }

    @Override
    public void play() {
        player.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        player.setPlayWhenReady(false);
    }

    @Override
    public void setVolume(float volume) {
        player.setVolume(volume);
    }

    @Override
    public void registerCallback(TvPlayer.Callback callback) {
        callbacks.add(callback);
    }

    @Override
    public void unregisterCallback(TvPlayer.Callback callback) {
        callbacks.remove(callback);
    }

    @Override
    public void seekTo(long positionMs) {
        player.seekTo(positionMs);
    }

    @Override
    public long getCurrentPosition() {
        return (player.getCurrentPosition());
    }

    @Override
    public void setSurface(Surface surface) {
        player.setVideoSurface(surface);
    }

    @Override
    public long getDuration() {
        return (player.getDuration());
    }

    @Override
    public void setPlaybackParams(PlaybackParams params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PlaybackParameters playbackParameters;

            try {
                playbackParameters = new PlaybackParameters(params.getSpeed(), params.getPitch());
            } catch (IllegalStateException is) {
                playbackParameters = PlaybackParameters.DEFAULT;
            }

            player.setPlaybackParameters(playbackParameters);
        }
    }

    /**
     * Stops the player if required
     */
    public void stop() {
        player.stop();
    }

    /**
     * Releases the resources used by the player
     */
    public void release() {
        player.release();
    }

    /**
     * Re-prepares a streaming for the player
     *
     * @param context the context needed to re-prepare the player
     */
    public void restart(Context context) {
        player.prepare(getMediaSource(context));
    }

    /**
     * Used to determine what is the main video format used by the player while streaming
     *
     * @return the video format used while streaming
     */
    public Format getVideoFormat() {
        return (player.getVideoFormat());
    }

    /**
     * Used to determine what is the main audio format used by the player while streaming.
     *
     * @return the audio format used while streaming
     */
    public Format getAudioFormat() {
        return (player.getAudioFormat());
    }

    /**
     * Adds listeners for eventual callbacks
     *
     * @param listener the given listener for callbacks.
     */
    public void addListener(Player.EventListener listener) {
        player.addListener(listener);
    }
}