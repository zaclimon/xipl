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
import android.net.Uri;

import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Extension of a {@link ExoPlayerAdapter} which incorporate the seeking of a MPEG-TS file if
 * the duration has been given beforehand.
 *
 * @author zaclimon
 * Creation date: 14/08/17
 */

public class SeekableTsExoPlayerAdapter extends ExoPlayerAdapter {

    private int mDurationMins;

    public SeekableTsExoPlayerAdapter(Context context) {
        super(context);
    }

    public SeekableTsExoPlayerAdapter(Context context, int durationMins) {
        super(context);
        mDurationMins = durationMins;
    }

    @Override
    public MediaSource onCreateMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(getContext(), "ExoPlayerAdapter");
        return (new ExtractorMediaSource(uri,
                new DefaultDataSourceFactory(getContext(), userAgent),
                new SeekableTsExtractorsFactory(mDurationMins),
                null,
                null));

    }

}
