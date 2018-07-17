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

import androidx.leanback.widget.PlaybackSeekDataProvider;

/**
 * Base {@link PlaybackSeekDataProvider} for seeking through a {@link androidx.leanback.media.PlaybackTransportControlGlue}
 *
 * @author zaclimon
 * Creation date: 31/07/17
 */

public class ProviderPlaybackSeekDataProvider extends PlaybackSeekDataProvider {

    // 10 seconds between each seeking steps.
    private static final int SEEK_STEPS_DURATION_MILLIS = 10000;

    private long[] durations;

    /**
     * Default constructor
     *
     * @param duration the total duration of the content.
     */
    public ProviderPlaybackSeekDataProvider(long duration) {
        init(duration);
    }

    /**
     * Initializes the Provider.
     *
     * @param duration the duration of the content.
     */
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
