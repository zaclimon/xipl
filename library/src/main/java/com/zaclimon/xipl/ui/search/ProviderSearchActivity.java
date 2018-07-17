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

package com.zaclimon.xipl.ui.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.leanback.app.SearchSupportFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.zaclimon.xipl.R;

/**
 * Activity in which a search for given content will occur.
 *
 * @author zaclimon
 * Creation date: 16/08/17
 */

public abstract class ProviderSearchActivity extends FragmentActivity {

    /**
     * Value to use if there is no custom theme set.
     */
    public static final int NO_THEME = -1;

    /**
     * Gets a custom {@link SearchSupportFragment} which will be used to get content.
     *
     * @return the corresponding fragment for searching.
     */
    protected abstract SearchSupportFragment getSearchFragment();

    /**
     * Defines the theme id to set for this {@link Activity}. Note that the no default theme might
     * be set with {@link #NO_THEME}
     *
     * @return the theme resource id or {@link #NO_THEME} if none set.
     */
    protected abstract int getThemeId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getThemeId() == NO_THEME) {
            setTheme(R.style.Theme_Leanback);
        } else {
            setTheme(getThemeId());
        }

        setContentView(R.layout.activity_search);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.activity_search_fragment, getSearchFragment());
        transaction.commit();
    }

    @Override
    public boolean onSearchRequested() {
        startActivity(new Intent(this, this.getClass()));
        return (true);
    }

}
