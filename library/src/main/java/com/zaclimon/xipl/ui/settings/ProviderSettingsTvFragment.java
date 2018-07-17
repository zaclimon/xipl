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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.leanback.app.RowsSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

/**
 * Fragment handling the Settings from a TV standpoint
 *
 * @author zaclimon
 * Creation date: 01/07/17
 */

public abstract class ProviderSettingsTvFragment extends RowsSupportFragment {

    private ArrayObjectAdapter mRowsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        mRowsAdapter.add(new ListRow(getSettingsObjectAdapter()));

        setAdapter(mRowsAdapter);
        setOnItemViewClickedListener(new SettingsItemClickListener());
    }

    /**
     * Method which generates an array adapter necessary to generate settings.
     *
     * The implementation can use {@link ProviderSettingsObjectAdapter} as a template
     * to add settings elements.
     *
     * @return the {@link ArrayObjectAdapter} which can be used to get setting elements.
     */
    protected abstract ArrayObjectAdapter getSettingsObjectAdapter();

    /**
     * Defines a method which extends {@link Activity} which will be used to handle every
     * settings section.
     *
     * The client will need to implement the activity as he/she wishes.
     *
     * @return the Activity managing all the settings section.
     */
    protected abstract Class<? extends Activity> getSettingsElementActivity();

    /**
     * Class offering an {@link OnItemViewClickedListener} for the various settings elements.
     *
     * @author zaclimon
     * Creation date: 01/07/17
     */
    private class SettingsItemClickListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Bundle) {
                // The item comes from a Settings element.
                Bundle bundle = (Bundle) item;
                Intent intent = new Intent(itemViewHolder.view.getContext(), getSettingsElementActivity());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }

}
