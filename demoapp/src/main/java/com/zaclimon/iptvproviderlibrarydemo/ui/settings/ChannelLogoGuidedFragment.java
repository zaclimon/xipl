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

package com.zaclimon.iptvproviderlibrarydemo.ui.settings;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.widget.Toast;

import com.google.android.media.tv.companionlibrary.EpgSyncJobService;
import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.utils.TvContractUtils;
import com.zaclimon.iptvproviderlibrarydemo.DemoConstants;
import com.zaclimon.iptvproviderlibrarydemo.R;
import com.zaclimon.iptvproviderlibrarydemo.service.DemoEpgService;

import java.util.List;


/**
 * Setting fragment which will either enable or disable the channel logos as seen on
 * the Live Channels application
 *
 * @author zaclimon
 * Creation date: 23/06/17
 */

public class ChannelLogoGuidedFragment extends GuidedStepFragment {

    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean logoActivated = sharedPreferences.getBoolean(DemoConstants.CHANNEL_LOGO_PREFERENCE, true);
        String title = getString(R.string.channel_logo_title);
        String description = getString(R.string.channel_logo_description);
        String breadcrumb;

        if (logoActivated) {
            breadcrumb = getString(R.string.current_status_text, getString(R.string.activated_text));
        } else {
            breadcrumb = getString(R.string.current_status_text, getString(R.string.deactivated_text));
        }

        return (new GuidanceStylist.Guidance(title, description, breadcrumb, null));
    }

    @Override
    public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
        GuidedAction.Builder yesAction = new GuidedAction.Builder(getActivity());
        GuidedAction.Builder noAction = new GuidedAction.Builder(getActivity());
        yesAction.title(R.string.yes_text);
        noAction.title(R.string.no_text);
        yesAction.id(GuidedAction.ACTION_ID_YES);
        noAction.id(GuidedAction.ACTION_ID_NO);
        actions.add(yesAction.build());
        actions.add(noAction.build());
    }

    @Override
    public void onGuidedActionClicked(GuidedAction guidedAction) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean initialAction = sharedPreferences.getBoolean(DemoConstants.CHANNEL_LOGO_PREFERENCE, true);

        if (guidedAction.getId() == GuidedAction.ACTION_ID_YES) {
            editor.putBoolean(DemoConstants.CHANNEL_LOGO_PREFERENCE, true);
        } else {
            editor.putBoolean(DemoConstants.CHANNEL_LOGO_PREFERENCE, false);
        }

        editor.apply();

        boolean modifiedAction = sharedPreferences.getBoolean(DemoConstants.CHANNEL_LOGO_PREFERENCE, true);

         /*
          Sync the channels to reflect the latest changes only if the stream type is different from
          before. Remove only the logos if the user doesn't want them. In that case, do an AsyncTask
          since it might freeze the user experience.
          */

        if (modifiedAction != initialAction) {

            if (!modifiedAction) {
                new AsyncRemoveLogos().execute();
            }

            String inputId = TvContract.buildInputId(DemoConstants.DEMO_TV_INPUT_COMPONENT);
            EpgSyncJobService.requestImmediateSync(getActivity(), inputId, new ComponentName(getActivity(), DemoEpgService.class));
            Toast.makeText(getActivity(), R.string.sync_channels, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            getActivity().finish();
        }
    }

    /**
     * Private class that will remove all Ace TV channels logo's from the system's database
     * in a asynchronous way.
     *
     * @author zaclimon
     *         Creation date: 23/06/17
     */
    private class AsyncRemoveLogos extends AsyncTask<Void, Void, Void> {

        public Void doInBackground(Void... params) {

            if (isAdded()) {
                ContentResolver contentResolver = getActivity().getContentResolver();
                List<Channel> channels = TvContractUtils.getChannels(contentResolver);

                for (Channel channel : channels) {
                    Uri channelLogoUri = TvContract.buildChannelLogoUri(channel.getId());
                    contentResolver.delete(channelLogoUri, null, null);
                }
            }
            return (null);
        }

    }
}
