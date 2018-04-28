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

package com.zaclimon.xipl;

import android.media.tv.TvContract;

import java.util.regex.Pattern;

/**
 * List of constants to be used for the library throughout the application's lifecycle.
 *
 * @author zaclimon
 * Creation date: 17/06/17
 */

public class Constants {

    // M3U file attributes
    public static final Pattern ATTRIBUTE_TVG_ID_PATTERN = Pattern.compile("tvg-id.\"(.*?)\"", Pattern.CASE_INSENSITIVE);
    public static final Pattern ATTRIBUTE_TVG_LOGO_PATTERN = Pattern.compile("tvg-logo.\"(.*?)\"", Pattern.CASE_INSENSITIVE);
    public static final Pattern ATTRIBUTE_TVG_NAME_PATTERN = Pattern.compile("tvg-name.\"(.*?)\"", Pattern.CASE_INSENSITIVE);
    public static final Pattern ATTRIBUTE_GROUP_TITLE_PATTERN = Pattern.compile("group-title.\"(.*?)\"", Pattern.CASE_INSENSITIVE);

    // Channel configuration stuff
    public static final String EPG_ID_PROVIDER = "epg_id";
    public static final String CHANNEL_GENRES_PROVIDER = "channel_genres";
    public static final String[] CHANNEL_GENRES = new String[]{
            TvContract.Programs.Genres.ANIMAL_WILDLIFE,
            TvContract.Programs.Genres.ARTS,
            TvContract.Programs.Genres.COMEDY,
            TvContract.Programs.Genres.DRAMA,
            TvContract.Programs.Genres.EDUCATION,
            TvContract.Programs.Genres.ENTERTAINMENT,
            TvContract.Programs.Genres.FAMILY_KIDS,
            TvContract.Programs.Genres.GAMING,
            TvContract.Programs.Genres.LIFE_STYLE,
            TvContract.Programs.Genres.MOVIES,
            TvContract.Programs.Genres.MUSIC,
            TvContract.Programs.Genres.NEWS,
            TvContract.Programs.Genres.PREMIER,
            TvContract.Programs.Genres.SHOPPING,
            TvContract.Programs.Genres.SPORTS,
            TvContract.Programs.Genres.TECH_SCIENCE,
            TvContract.Programs.Genres.TRAVEL
    };

    private Constants(){}
}