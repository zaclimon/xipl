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

package com.zaclimon.xipldemo.ui.settings;

import com.zaclimon.xipl.ui.settings.ProviderSettingsObjectAdapter;
import com.zaclimon.xipldemo.R;

/**
 * Demo {@link ProviderSettingsObjectAdapter} used to showcase how settings sections can be
 * generated.
 *
 * @author zaclimon
 * Creation date: 09/08/17
 */

public class DemoSettingsObjectAdapter extends ProviderSettingsObjectAdapter {

    /**
     * Default constructor
     */
    public DemoSettingsObjectAdapter() {
        addSection(R.string.channel_logo_title, R.drawable.ic_channel_logo);
        addSection(R.string.about_text, R.drawable.ic_about);
    }

}
