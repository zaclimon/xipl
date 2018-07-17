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

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.zaclimon.xipl.R;
import com.zaclimon.xipl.util.ActivityUtil;

/**
 * Activity responsible for VOD playback.
 *
 * @author zaclimon
 * Creation date: 11/08/17
 */

public abstract class VodPlaybackActivity extends FragmentActivity {

    public static final int NO_THEME = -1;

    /**
     * Defines the theme id to set for this {@link Activity}. Note that the no theme might be set
     * with {@link #NO_THEME}
     *
     * @return the theme resource id or {@link #NO_THEME} if none set.
     */
    protected abstract int getThemeId();

    /**
     * Defines a {@link VodPlaybackFragment} that will be used to playback the given content.
     *
     * @return the fragment that will play the given content.
     */
    protected abstract VodPlaybackFragment getVodPlaybackFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityUtil.isTvMode(this) && getThemeId() != NO_THEME) {
            setTheme(getThemeId());
        } else if (ActivityUtil.isTvMode(this)) {
            setTheme(R.style.Theme_Leanback);
        }

        setContentView(R.layout.activity_vod_playback);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment playbackFragment = getVodPlaybackFragment();
        Bundle arguments = getIntent().getExtras();
        playbackFragment.setArguments(arguments);
        fragmentTransaction.add(R.id.activity_vod_playback_fragment, playbackFragment);

        fragmentTransaction.commit();

    }

}
