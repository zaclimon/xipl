package com.zaclimon.iptvproviderlibrary.ui.vod;

import android.app.Activity;
import android.os.Build;
import android.support.v17.leanback.media.PlaybackTransportControlGlue;
import android.support.v17.leanback.media.PlayerAdapter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackSeekDataProvider;
import android.widget.Toast;

/**
 * Base {@link PlaybackSeekDataProvider} for seeking through a {@link ProviderVideoMediaPlayerGlue}
 *
 * @author zaclimon
 * Creation date: 31/07/17
 */

public class ProviderPlaybackSeekDataProvider extends PlaybackSeekDataProvider {

    // 10 seconds between each seeking steps.
    private static final int SEEK_STEPS_DURATION_MILLIS = 10000;

    private long[] durations;

    public ProviderPlaybackSeekDataProvider(long duration) {
        init(duration);
    }

    private void init(long duration) {
        int steps = (int) (duration / SEEK_STEPS_DURATION_MILLIS);
        durations = new long[steps];

        int currentMillis = 0;

        for (int i = 0; i < durations.length; i++) {
            durations[i] = currentMillis;
            currentMillis += SEEK_STEPS_DURATION_MILLIS;
        }
    }

    @Override
    public long[] getSeekPositions() {
        return (durations);
    }

}
