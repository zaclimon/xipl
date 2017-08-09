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

package com.zaclimon.iptvproviderlibrarydemo.ui.urlinput;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;

import com.zaclimon.iptvproviderlibrarydemo.DemoConstants;
import com.zaclimon.iptvproviderlibrarydemo.R;
import com.zaclimon.iptvproviderlibrarydemo.ui.DemoMainActivity;

import java.util.List;

/**
 * Fragment in which a user can write a Xtream Codes compatible M3U playlist.
 *
 * @author zaclimon
 * Creation date: 09/08/17
 */

public class UrlInputGuidedFragment extends GuidedStepFragment {

    private final int ACTION_PLAYLIST_URL = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String url = sharedPreferences.getString(DemoConstants.PLAYLIST_URL_PREFERENCE, "");

        if (!TextUtils.isEmpty(url)) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }

    }

    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = getString(R.string.enter_playlist_url);
        return (new GuidanceStylist.Guidance(title, null, null, null));
    }

    @Override
    public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
        GuidedAction.Builder playlistUrl = new GuidedAction.Builder(getActivity());
        playlistUrl.title(R.string.playlist_url);
        playlistUrl.editTitle("");
        playlistUrl.editable(true);
        playlistUrl.editInputType(InputType.TYPE_CLASS_TEXT);
        playlistUrl.id(ACTION_PLAYLIST_URL);
        actions.add(playlistUrl.build());
    }

    @Override
    public long onGuidedActionEditedAndProceed(GuidedAction action) {

        int id = (int) action.getId();

        if (id == ACTION_PLAYLIST_URL) {

            String url = action.getEditTitle().toString();

            if (!TextUtils.isEmpty(url) && Patterns.WEB_URL.matcher(url).matches()) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putString(DemoConstants.PLAYLIST_URL_PREFERENCE, url);
                editor.apply();

                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();

            } else {
                action.setTitle(getString(R.string.playlist_url_invalid));
            }
        }
        return (GuidedAction.ACTION_ID_CURRENT);
    }

}
