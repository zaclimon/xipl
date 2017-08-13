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

package com.zaclimon.xipldemo.ui;

import android.support.v17.leanback.app.RowsFragment;

import com.zaclimon.xipl.ui.main.ProviderTvFragment;
import com.zaclimon.xipldemo.R;
import com.zaclimon.xipldemo.ui.settings.DemoProviderSettingsTvFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Showcase fragment to show how a {@link ProviderTvFragment} can be implemented
 *
 * @author zaclimon
 * Creation date: 09/08/17
 */

public class DemoMainFragment extends ProviderTvFragment {

    @Override
    public String getAppName() {
        return (getString(R.string.app_name));
    }

    @Override
    public Map<String, RowsFragment> getFragmentMap() {
        Map<String, RowsFragment> tempMap = new HashMap<>();
        tempMap.put(getString(R.string.settings_text), new DemoProviderSettingsTvFragment());
        return (tempMap);
    }


}
