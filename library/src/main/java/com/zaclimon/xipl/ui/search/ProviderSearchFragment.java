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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.app.SearchFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.SpeechRecognitionCallback;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zaclimon.xipl.R;
import com.zaclimon.xipl.model.AvContent;
import com.zaclimon.xipl.persistence.ContentPersistence;
import com.zaclimon.xipl.ui.components.cardview.CardViewImageProcessor;
import com.zaclimon.xipl.ui.components.cardview.CardViewPresenter;
import com.zaclimon.xipl.ui.components.listener.AvContentTvItemClickListener;
import com.zaclimon.xipl.ui.vod.VodPlaybackActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A base {@link SearchFragment} which can be used for searching content.
 *
 * @author zaclimon
 * Creation date: 16/08/17
 */

public abstract class ProviderSearchFragment extends SearchFragment implements SearchFragment.SearchResultProvider {

    /**
     * Value used to display the search results as a single row.
     */
    public static final int SEARCH_LAYOUT_SINGLE_ROW = 0;

    /**
     * Value used to display the search results based on their group.
     */
    public static final int SEARCH_LAYOUT_GROUP_ROW = 1;

    private static final int REQUEST_SPEECH = 0;

    private ArrayObjectAdapter mRowsAdapter;
    private FrameLayout mFrameLayout;
    private View mEmptyResultsView;
    private int mSearchLayout = SEARCH_LAYOUT_GROUP_ROW;

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

    /**
     * Determines the layout of the search results.
     *
     * {@link ProviderSearchFragment#SEARCH_LAYOUT_SINGLE_ROW} determines a single row layout
     * {@link ProviderSearchFragment#SEARCH_LAYOUT_GROUP_ROW} determines a multiple row layout based on the group of the content.
     */
    protected void setSearchLayout(int layout) {
        if (!(layout == SEARCH_LAYOUT_SINGLE_ROW || layout == SEARCH_LAYOUT_GROUP_ROW)) {
            throw new IllegalArgumentException("Invalid layout: " + layout);
        }
        mSearchLayout = layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        mEmptyResultsView = View.inflate(getActivity(), R.layout.view_content_unavailable, null);
        TextView textView = mEmptyResultsView.findViewById(R.id.view_content_unavailable_textview);
        textView.setText(R.string.no_results_found);
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
    public void onStart() {
        super.onStart();
        if (isAdded() && getView() != null) {
            mFrameLayout = getView().findViewById(R.id.lb_search_frame);
        }
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
        new AsyncUpdateRows(newQuery).execute();
        return (true);
    }

    /**
     * Shows a view in which search results could not have been found.
     */
    protected void showNoResultsView() {
        if (isAdded() && mFrameLayout != null) {
            mFrameLayout.addView(mEmptyResultsView);
        }
    }

    /**
     * Removes a view in which search results could not have been found.
     */
    protected void removeNoResultsView() {
        if (isAdded() && mFrameLayout != null) {
            mFrameLayout.removeView(mEmptyResultsView);
        }
    }

    /**
     * AsyncTask responsible for updating rows in a way that it does not disrupt the user experience.
     *
     * @author zaclimon
     * Creation date: 17/08/17
     */
    private class AsyncUpdateRows extends AsyncTask<Void, Void, List<ListRow>> {

        private String mQuery;

        public AsyncUpdateRows(String query) {
            mQuery = query;
        }

        @Override
        public void onPreExecute() {
            mRowsAdapter.clear();
        }

        @Override
        public List<ListRow> doInBackground(Void... params) {

            ArrayObjectAdapter arrayObjectAdapter;
            HeaderItem headerItem;
            List<ListRow> tempList = new ArrayList<>();

            if (mSearchLayout == SEARCH_LAYOUT_GROUP_ROW) {

                 /*
                  Set everything in a map first for easy search/insertion for the values.
                  Use a TreeMap since we care about the natural order of the keys.

                  Also get the content based on their inserted order since some of them might be
                  sorted by date.
                  */

                Map<String, List<AvContent>> contentMap = new TreeMap<>();
                List<AvContent> contents = getContentPersistence().searchTitle(mQuery, false);

                for (AvContent content : contents) {
                    List<AvContent> tempGroupList;

                    if (!contentMap.containsKey(content.getGroup())){
                        tempGroupList = new ArrayList<>();
                    } else {
                        tempGroupList = contentMap.get(content.getGroup());
                    }
                    tempGroupList.add(content);
                    contentMap.put(content.getGroup(), tempGroupList);
                }

                for (Map.Entry<String, List<AvContent>> entry : contentMap.entrySet()) {
                    headerItem = new HeaderItem(entry.getKey());
                    arrayObjectAdapter = new ArrayObjectAdapter(new CardViewPresenter(getCardViewImageProcessor()));
                    arrayObjectAdapter.addAll(0, entry.getValue());
                    tempList.add(new ListRow(headerItem, arrayObjectAdapter));
                }
            } else {
                headerItem = new HeaderItem(getString(R.string.search_results));
                arrayObjectAdapter = new ArrayObjectAdapter(new CardViewPresenter(getCardViewImageProcessor()));
                arrayObjectAdapter.addAll(0, getContentPersistence().searchTitle(mQuery, true));
                tempList.add(new ListRow(headerItem, arrayObjectAdapter));
            }
            return (tempList);
        }

        @Override
        public void onPostExecute(List<ListRow> results) {
            if (!results.isEmpty()) {
                removeNoResultsView();
                mRowsAdapter.addAll(0, results);
            } else if (mEmptyResultsView.getParent() == null) {
                // Show only the no results view if it has a parent.
                showNoResultsView();
            }
        }

    }

}
