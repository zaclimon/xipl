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

package com.zaclimon.xipl.util;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;

import static android.content.Context.UI_MODE_SERVICE;

/**
 * Utility class most likely to be used by activities.
 *
 * @author zaclimon
 * Creation date: 25/06/17
 */

public class ActivityUtil {

    /**
     * Verifies if the current user interface (UI) mode is for television (Mostly if we're in
     * Android TV)
     *
     * @param activity the activity verifying the UI mode.
     * @return true if the application is running in Android TV.
     */
    public static boolean isTvMode(Activity activity) {
        UiModeManager uiModeManager = (UiModeManager) activity.getSystemService(UI_MODE_SERVICE);
        return (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);
    }

    /**
     * Converts given density-independent pixels (dp) to pixels
     *
     * @param dp      the number of dp to convert
     * @param context the required context to convert the dp
     * @return the number of pixel for the given dp.
     */
    public static int dpToPixel(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return ((int) (dp * density + 0.5f));
    }

}