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

import android.os.Bundle;
import android.support.v17.leanback.app.SearchFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.SpeechRecognitionCallback;

import com.zaclimon.xipl.R;
import com.zaclimon.xipl.persistence.ContentPersistence;
import com.zaclimon.xipl.ui.components.cardview.CardViewImageProcessor;
import com.zaclimon.xipl.ui.components.cardview.CardViewPresenter;
import com.zaclimon.xipl.ui.components.listener.AvContentTvItemClickListener;
import com.zaclimon.xipl.ui.vod.VodPlaybackActivity;

/**
 * A base {@link SearchFragment} which can be used for searching content.
 *
 * @author zaclimon
 * Creation date: 16/08/17
 */

public abstract class ProviderSearchFragment extends SearchFragment implements SearchFragment.SearchResultProvider {

    private static final int REQUEST_SPEECH = 0;

    private ArrayObjectAdapter mRowsAdapter;

    /**
     * Gets the implementation used to persist content
     *
     * @return the content persistence medium
     */
    protected abstract ContentPersistence getContentPersistence();

    /**
     * Gets the CardView image processor required to fetch images
     *
     * @return the image processor for this.
     */
    protected abstract CardViewImageProcessor getCardViewImageProcessor();

    /**
     * Gets the playback Activity required to play a given VOD content
     *
     * @return the playback activity
     */
    protected abstract Class<? extends VodPlaybackActivity> getPlaybackActivity();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setSearchResultProvider(this);
        setOnItemViewClickedListener(new AvContentTvItemClickListener(getPlaybackActivity()));
        setSpeechRecognitionCallback(new SpeechRecognitionCallback() {
            @Override
            public void recognizeSpeech() {
                startActivityForResult(getRecognizerIntent(), REQUEST_SPEECH);
            }
        });
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return (mRowsAdapter);
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        return (false);
    }

    @Override
    public boolean onQueryTextSubmit(String newQuery) {
        HeaderItem headerItem = new HeaderItem(getString(R.string.search_results));
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new CardViewPresenter(getCardViewImageProcessor()));
        mRowsAdapter.clear();

        arrayObjectAdapter.addAll(0, getContentPersistence().searchTitle(newQuery, true));
        mRowsAdapter.add(new ListRow(headerItem, arrayObjectAdapter));
        return (true);
    }

}
