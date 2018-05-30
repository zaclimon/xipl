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

package com.zaclimon.xipl.properties;

import com.google.android.media.tv.companionlibrary.model.Channel;

/**
 * Gives the possibility to a IPTV provider an easier interface for using
 * {@link com.zaclimon.xipl.util.ProviderChannelUtil}
 *
 * @author zaclimon
 * Creation date: 08/08/17
 */

public interface ChannelProperties {

    /**
     * Notifies if the channel should have a logo when getting generated
     *
     * @return true if the channel should have a logo during creation
     */
    boolean hasChannelLogo();

    /**
     * Determines if the channel is getting live content.
     *
     * @param channel the channel that is getting checked
     * @return true if the channel gives live content.
     */
    boolean isLiveChannel(Channel channel);

    /**
     * Determines if the region for a channel is valid. Can be useful if filtering some regions for
     * example.
     *
     * @param channel The channel that will be get checked.
     * @return true if the region for a given channel is valid.
     */
    boolean isChannelRegionValid(Channel channel);

    /**
     * Determines if a channel's genre is valid. Can be useful for users that only want to see
     * a particular genre without filtering each channel individually.
     *
     * @param channel the channel that will be checked
     * @return true if the channel's genre is valid
     */
    boolean isChannelGenreValid(Channel channel);

    /**
     * Determines if a channel's playlist group can be added to the channel list. Can offer a bit more
     * granularity compared to channel region filtering for example.
     *
     * @param channel the channel that will be checked
     * @return true if the channel's group is valid.
     */
    boolean isChannelGroupValid(Channel channel);

}
