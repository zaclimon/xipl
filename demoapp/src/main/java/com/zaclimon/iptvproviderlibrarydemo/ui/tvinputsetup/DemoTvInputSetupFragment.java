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

package com.zaclimon.iptvproviderlibrarydemo.ui.tvinputsetup;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.tv.TvInputInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.media.tv.companionlibrary.ChannelSetupFragment;
import com.google.android.media.tv.companionlibrary.EpgSyncJobService;
import com.zaclimon.iptvproviderlibrary.service.ProviderEpgService;
import com.zaclimon.iptvproviderlibrarydemo.DemoConstants;
import com.zaclimon.iptvproviderlibrarydemo.R;
import com.zaclimon.iptvproviderlibrarydemo.service.DemoEpgService;
import com.zaclimon.iptvproviderlibrarydemo.ui.urlinput.UrlInputActivity;

import java.net.URL;

/**
 * Demo Fragment used to show a channel configuration progress.
 *
 * @author zaclimon
 * Creation Date: 09/08/17
 */

public class DemoTvInputSetupFragment extends ChannelSetupFragment {

    private final int URL_REQUEST_CODE = 0;
    private String mInputId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInputId = getActivity().getIntent().getStringExtra(TvInputInfo.EXTRA_INPUT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = super.onCreateView(inflater, container, savedInstanceState);
        setChannelListVisibility(true);
        setTitle(R.string.app_name);
        return (fragmentView);
    }

    @Override
    public void onScanStarted() {
        Intent intent = new Intent(getActivity(), UrlInputActivity.class);
        startActivityForResult(intent, URL_REQUEST_CODE);
    }

    @Override
    public void onScanFinished() {
        getActivity().finish();
    }

    @Override
    public void onScanError(int reason) {

        if (reason == EpgSyncJobService.ERROR_NO_CHANNELS) {
            Toast.makeText(getActivity(), R.string.channel_sync_failure, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

    }

    @Override
    public String getInputId() {
        return (mInputId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == URL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                EpgSyncJobService.cancelAllSyncRequests(getActivity());
                EpgSyncJobService.requestImmediateSync(getActivity(), mInputId, new ComponentName(getActivity(), DemoEpgService.class));
            } else {
                getActivity().finish();
            }
        }

    }
}
