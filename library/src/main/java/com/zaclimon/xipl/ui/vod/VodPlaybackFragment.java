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

package com.zaclimon.xipl.ui.vod;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.leanback.app.ErrorSupportFragment;
import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.fragment.app.FragmentTransaction;
import android.util.DisplayMetrics;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.zaclimon.xipl.R;
import com.zaclimon.xipl.properties.VodProperties;

/**
 * Fragment responsible for a provider's VOD content playback
 *
 * @author zaclimon
 * Creation date: 11/08/17
 */

public abstract class VodPlaybackFragment extends VideoSupportFragment {

    PlaybackTransportControlGlue<LeanbackPlayerAdapter> mPlayerGlue;
    SimpleExoPlayer mSimpleExoPlayer;

    /**
     * Retrieves the properties for a given VOD content
     *
     * @return the properties for a given content
     */
    protected abstract VodProperties getVodProperties();

    /**
     * Retrieves the provider name for contact info if an error happens
     *
     * @return the provider name for this media content
     */
    protected abstract String getProviderName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getVodProperties().isExternalPlayerUsed()) {
            configureExternalPlayer();
            getActivity().finish();
        } else {
            configureInternalPlayer();
        }
    }

    /**
     * Sets up the usage of the internal player used by the library.
     */
    protected void configureInternalPlayer() {
        Bundle arguments = getArguments();
        String url = arguments.getString(VodTvSectionFragment.AV_CONTENT_LINK_BUNDLE);

        // Configure the ExoPlayer instance that will be used to play the media
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), getActivity().getApplicationInfo().loadLabel(getActivity().getPackageManager()).toString()));
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getActivity()), new DefaultTrackSelector(), new DefaultLoadControl());
        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(dataSourceFactory);
        Uri uri = Uri.parse(url);
        mSimpleExoPlayer.prepare(factory.createMediaSource(uri));
        mSimpleExoPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == Player.STATE_READY && mPlayerGlue.getSeekProvider() == null) {
                    mPlayerGlue.setSeekProvider(new ProviderPlaybackSeekDataProvider(mPlayerGlue.getDuration()));

                    // Force content to fit to screen if wanted.
                    if (getVodProperties().isVideoFitToScreen()) {
                        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
                        mPlayerGlue.getPlayerAdapter().getCallback().onVideoSizeChanged(mPlayerGlue.getPlayerAdapter(), displayMetrics.widthPixels, displayMetrics.heightPixels);
                    }
                }
            }
        });

        // Configure Leanback for playback. Use the updatePeriodMs used before in ExoPlayerAdapter
        LeanbackPlayerAdapter playerAdapter = new LeanbackPlayerAdapter(getActivity(), mSimpleExoPlayer, 16);
        mPlayerGlue = new PlaybackTransportControlGlue<>(getActivity(), playerAdapter);
        mPlayerGlue.setHost(new VideoSupportFragmentGlueHost(this));
        mPlayerGlue.setTitle(arguments.getString(VodTvSectionFragment.AV_CONTENT_TITLE_BUNDLE));
        mPlayerGlue.setSubtitle(arguments.getString(VodTvSectionFragment.AV_CONTENT_GROUP_BUNDLE));

        setBackgroundType(BG_LIGHT);
        mPlayerGlue.playWhenPrepared();
    }

    /**
     * Sets up the usage of an external player installed on the device.
     */
    protected void configureExternalPlayer() {
        Uri contentUri = Uri.parse(getArguments().getString(VodTvSectionFragment.AV_CONTENT_LINK_BUNDLE));
        Intent intent = new Intent(Intent.ACTION_VIEW, contentUri);
        intent.setDataAndType(contentUri, "video/*");
        startActivity(intent);
    }

    @Override
    public void onPause() {
        if (mPlayerGlue != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !getActivity().isInPictureInPictureMode()) {
                mPlayerGlue.pause();
            }
        }
        super.onPause();
    }

    @Override
    public void onError(int errorCode, CharSequence errorMessage) {

        // Notify the user if a video can't be played.
        if (errorCode == ExoPlaybackException.TYPE_SOURCE) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            ErrorSupportFragment errorFragment = new ErrorSupportFragment();
            errorFragment.setDefaultBackground(true);
            errorFragment.setMessage(getString(R.string.video_not_playable, getProviderName()));
            errorFragment.setImageDrawable(getActivity().getDrawable(R.drawable.lb_ic_sad_cloud));
            fragmentTransaction.replace(R.id.activity_vod_playback_fragment, errorFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.release();
        }
    }

}
