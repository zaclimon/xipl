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

package com.zaclimon.iptvproviderlibrarydemo.service;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.zaclimon.iptvproviderlibrary.ChannelProperties;
import com.zaclimon.iptvproviderlibrary.service.ProviderEpgService;
import com.zaclimon.iptvproviderlibrarydemo.DemoChannelPropertiesImpl;
import com.zaclimon.iptvproviderlibrarydemo.DemoConstants;

/**
 * Demo Service to sync EPG for the Android TV's system database.
 *
 * @author zaclimon
 * Creation date: 09/08/17
 */

public class DemoEpgService extends ProviderEpgService {

    @Override
    public String getPlaylistUrl() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return (sharedPreferences.getString(DemoConstants.PLAYLIST_URL_PREFERENCE, ""));
    }

    @Override
    public String getEpgUrl() {
        return (null);
    }

    @Override
    public ChannelProperties getChannelProperties() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return (new DemoChannelPropertiesImpl(sharedPreferences));
    }

}
