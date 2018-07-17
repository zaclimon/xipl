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

package com.zaclimon.xipl.ui.settings;

import android.os.Bundle;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.leanback.widget.ArrayObjectAdapter;

import com.zaclimon.xipl.ui.components.cardview.CardViewPresenter;

import java.util.List;

/**
 * Class extending {@link ArrayObjectAdapter} which gives a list of current
 * settings elements.
 * <p>
 * It's main view is a {@link CardViewPresenter}
 *
 * @author zaclimon
 * Creation date: 21/06/17
 */

public abstract class ProviderSettingsObjectAdapter extends ArrayObjectAdapter {

    /**
     * Static variable for identifying the name id.
     */
    public static final String BUNDLE_SETTINGS_NAME_ID = "bundle_name_id";

    /**
     * Static variable for identifying the drawable id.
     */
    public static final String BUNDLE_SETTINGS_DRAWABLE_ID = "bundle_drawable_id";

    private final String LOG_TAG = getClass().getSimpleName();

    /**
     * Default constructor. Useful if adding the sections manually
     */
    public ProviderSettingsObjectAdapter() {
        super(new CardViewPresenter());
    }

    /**
     * Additional constructor used if the sections are in batch
     * @param sections the list of sections to add to the settings.
     */
    public ProviderSettingsObjectAdapter(List<Bundle> sections) {
        super(new CardViewPresenter());
        validateBundles(sections);
        addAll(0, sections);
    }

    /**
     * Defines a section to be added to this object adapter
     *
     * @param stringId the id of the resource string for this section
     * @param drawableId the id of the resource drawable for this section
     */
    public void addSection(@StringRes int stringId, @DrawableRes int drawableId) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_SETTINGS_NAME_ID, stringId);
        bundle.putInt(BUNDLE_SETTINGS_DRAWABLE_ID, drawableId);
        add(bundle);
    }

    /**
     * Verifies if the given bundle has the correct attributes for a given Settings section.
     *
     * @param sections is the list of settings sections
     * @return true if the bundles containing the sections are valid.
     */
    private boolean validateBundles(List<Bundle> sections) {

        for (Bundle bundle : sections) {
            if (!bundle.containsKey(BUNDLE_SETTINGS_NAME_ID) || !bundle.containsKey(BUNDLE_SETTINGS_DRAWABLE_ID)) {
                throw new IllegalArgumentException("Section not valid!");
            }
        }
        return (true);
    }

}
