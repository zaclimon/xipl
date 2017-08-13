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

import android.app.Activity;
import android.support.v17.leanback.widget.ArrayObjectAdapter;

import com.zaclimon.xipl.ui.settings.ProviderSettingsTvFragment;

/**
 * Fragment to showcase how one can create a custom object array adapter for a given settings
 * section of an app.
 *
 * @author zaclimon
 * Creation date: 09/08/17
 */

public class DemoProviderSettingsTvFragment extends ProviderSettingsTvFragment {

    @Override
    public Class<? extends Activity> getSettingsElementActivity() {
        return (DemoSettingsElementActivity.class);
    }

    @Override
    public ArrayObjectAdapter getSettingsObjectAdapter() {
        return (new DemoSettingsObjectAdapter());
    }

}
