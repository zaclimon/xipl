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

import android.app.Activity;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;

import com.zaclimon.iptvproviderlibrary.ui.settings.ProviderSettingsObjectAdapter;
import com.zaclimon.iptvproviderlibrarydemo.R;

/**
 * Demo Activity which is used to handle any given elements in a {@link ProviderSettingsObjectAdapter}
 *
 * @author zaclimon
 * Creation date: 09/08/17
 */

public class DemoSettingsElementActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras.containsKey(ProviderSettingsObjectAdapter.BUNDLE_SETTINGS_NAME_ID) && extras.containsKey(ProviderSettingsObjectAdapter.BUNDLE_SETTINGS_DRAWABLE_ID)) {

                int nameId = extras.getInt(ProviderSettingsObjectAdapter.BUNDLE_SETTINGS_NAME_ID);

                switch (nameId) {
                    case R.string.channel_logo_title:
                        GuidedStepFragment.addAsRoot(this, new ChannelLogoGuidedFragment(), android.R.id.content);
                        break;
                    case R.string.about_text:
                        GuidedStepFragment.addAsRoot(this, new AboutGuidedFragment(), android.R.id.content);
                        break;
                }
            }
        }
    }
}
