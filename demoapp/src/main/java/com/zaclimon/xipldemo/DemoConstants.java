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

package com.zaclimon.xipldemo;

import android.content.ComponentName;

import com.zaclimon.xipl.service.ProviderTvInputService;

/**
 * Constants class used for the demo app for aipl.
 *
 * @author zaclimon
 * Creation date: 09/08/17
 */

public class DemoConstants {

    // Preferences
    public static final String CHANNEL_LOGO_PREFERENCE = "channel_logo";
    public static final String PLAYLIST_URL_PREFERENCE = "playlist_url";

    // Channel configuration
    public static final ComponentName DEMO_TV_INPUT_COMPONENT = new ComponentName("com.zaclimon.iptvproviderlibrarydemo", ProviderTvInputService.class.getName());
}
