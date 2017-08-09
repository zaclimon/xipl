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

package com.zaclimon.iptvproviderlibrarydemo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.media.tv.companionlibrary.model.Channel;
import com.zaclimon.iptvproviderlibrary.ChannelProperties;
import com.zaclimon.iptvproviderlibrary.Constants;

/**
 * Concrete implementation of the {@link ChannelProperties} interface for interacting with a given
 * {@link Channel}
 *
 * @author zaclimon
 * Creation Date: 09/08/17
 */

public class DemoChannelPropertiesImpl implements ChannelProperties {

    private SharedPreferences mSharedPreferences;

    public DemoChannelPropertiesImpl(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    @Override
    public boolean hasChannelLogo() {
        return (mSharedPreferences.getBoolean(DemoConstants.CHANNEL_LOGO_PREFERENCE, true));
    }

    @Override
    public boolean isLiveChannel(Channel channel) {
        // An Xtream Codes live channel has a live identifier in it's URL.
        return (channel.getInternalProviderData() == null || channel.getInternalProviderData().getVideoUrl().contains("/live/"));
    }

    @Override
    public boolean isChannelRegionValid(Channel channel) {
        return (true);
    }

}
