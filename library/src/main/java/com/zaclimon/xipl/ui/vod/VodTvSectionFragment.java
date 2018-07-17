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

package com.zaclimon.xipl.ui.vod;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.app.ProgressBarManager;
import androidx.leanback.app.RowsSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.DiffCallback;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ScaleFrameLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.zaclimon.xipl.R;
import com.zaclimon.xipl.model.AvContent;
import com.zaclimon.xipl.persistence.ContentPersistence;
import com.zaclimon.xipl.ui.components.cardview.CardViewImageProcessor;
import com.zaclimon.xipl.ui.components.cardview.CardViewPresenter;
import com.zaclimon.xipl.ui.components.listener.AvContentTvItemClickListener;
import com.zaclimon.xipl.util.AvContentUtil;
import com.zaclimon.xipl.util.RichFeedUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Base class in which VOD-like (Video on demand) fragments can base off in order to have a complete
 * list of content based on their provider's catalog.
 *
 * @author zaclimon
 * Creation date: 05/07/17
 */

public abstract class VodTvSectionFragment extends RowsSupportFragment {

    /**
     * Variable for accessing an {@link AvContent} title
     */
    public static final String AV_CONTENT_TITLE_BUNDLE = "av_content_title";

    /**
     * Variable for accessing an {@link AvContent} logo url
     */
    public static final String AV_CONTENT_LOGO_BUNDLE = "av_content_logo";

    /**
     * Variable for accessing an {@link AvContent} content url
     */
    public static final String AV_CONTENT_LINK_BUNDLE = "av_content_link";

    /**
     * Variable for accessing an {@link AvContent} group (provider)
     */
    public static final String AV_CONTENT_GROUP_BUNDLE = "av_content_group";

    private final String LOG_TAG = getClass().getSimpleName();

    private ArrayObjectAdapter mRowsAdapter;
    private ProgressBarManager mProgressBarManager;
    private AsyncProcessAvContent mAsyncProcessAvContent;
    private ScaleFrameLayout mScaleFrameLayout;

    /**
     * Gets the link to retrieve an M3U playlist from a given endpoint
     *
     * @return the link to to retrieve VOD content.
     */
    protected abstract String getVodContentApiLink();

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
    protected abstract CardViewImageProcessor getImageProcessor();

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
        mProgressBarManager = ((BrowseSupportFragment) getParentFragment()).getProgressBarManager();
        mScaleFrameLayout = getActivity().findViewById(R.id.scale_frame);
        mProgressBarManager.setRootView((ViewGroup) getActivity().findViewById(R.id.browse_container_dock));
        setOnItemViewClickedListener(new AvContentTvItemClickListener(getPlaybackActivity()));
        setAdapter(mRowsAdapter);
        showRowsAdapter();
        mAsyncProcessAvContent = new AsyncProcessAvContent();
        mAsyncProcessAvContent.execute();

    }

    /**
     * Shows the current content to the user's screen.
     */
    private void showRowsAdapter() {
        Map<String, List<AvContent>> tempMap = getContentsByGroup();

        if (!tempMap.isEmpty()) {
            mProgressBarManager.hide();
            for (String group : tempMap.keySet()) {
                addContentRow(tempMap, group);
            }
        }
    }
    /**
     * Updates the content shown to the user.
     */
    private void updateRowsAdapter() {
        Map<String, List<AvContent>> tempMap = getContentsByGroup();

        if (!tempMap.isEmpty()) {
            Set<String> groups = tempMap.keySet();
            int i = 0;
            int rowsAdapterSize = mRowsAdapter.size();
            mProgressBarManager.hide();

            for (String group : groups) {
                // It might be possible that a new content row is being added while the rows have been generated.
                if (i >= rowsAdapterSize) {
                    addContentRow(tempMap, group);
                } else {
                    ListRow tempListRow = (ListRow) mRowsAdapter.get(i);
                    ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) tempListRow.getAdapter();
                    objectAdapter.setItems(tempMap.get(group), getCallback());
                }
                i++;
            }
        }
    }

    /**
     * Obtains a map containing the list of contents. Each list of contents are categorized by
     * content groups as defined in {@link AvContent}
     *
     * @return the map of contents based on their respective group.
     */
    private Map<String, List<AvContent>> getContentsByGroup() {
        // Use a TreeMap due to the alphabetical listing of contents.
        Map<String, List<AvContent>> tempMap = new TreeMap<>();
        List<AvContent> persistenceContents = getContentPersistence().getFromCategory(VodTvSectionFragment.this.getClass().getSimpleName(), true);

        if (!persistenceContents.isEmpty()) {
            List<AvContent> groupContents = new ArrayList<>();
            String currentGroup = persistenceContents.get(0).getGroup();

            for (AvContent content : persistenceContents) {
                if (!currentGroup.equals(content.getGroup())) {
                    tempMap.put(currentGroup, groupContents);
                    currentGroup = content.getGroup();
                    groupContents = new ArrayList<>();
                }
                groupContents.add(content);
            }
        }
        return (tempMap);
    }

    private DiffCallback<AvContent> getCallback() {
        DiffCallback<AvContent> callback = new DiffCallback<AvContent>() {
            @Override
            public boolean areItemsTheSame(@NonNull AvContent oldItem, @NonNull AvContent newItem) {
                return (oldItem.getId() == newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull AvContent oldItem, @NonNull AvContent newItem) {
                return (oldItem.getContentLink().equals(newItem.getContentLink()));
            }
        };
        return (callback);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Cancel the processing and hide the progress bar if we're changing rows for example.
        if (mAsyncProcessAvContent.getStatus() == AsyncTask.Status.RUNNING) {
            mAsyncProcessAvContent.cancel(true);
            mProgressBarManager.hide();
        }

        if (mRowsAdapter.size() == 0 && mScaleFrameLayout != null) {
            mScaleFrameLayout.removeAllViews();
        }
    }

    /**
     * Adds a new {@link ArrayObjectAdapter} for a new VOD content category
     *
     * @param tempMap the map used to retrieve contents
     * @param group the name of the VOD content group
     */
    private void addContentRow(Map<String, List<AvContent>> tempMap, String group) {
        if (mRowsAdapter != null) {
            ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new CardViewPresenter(getImageProcessor()));
            HeaderItem header = new HeaderItem(group);
            arrayObjectAdapter.addAll(0, tempMap.get(group));
            mRowsAdapter.add(new ListRow(header, arrayObjectAdapter));
        }
    }

    /**
     * Shows a view in which content is not available and hides the progress bar if it was shown.
     */
    private void showErrorView() {
        if (isAdded() && mScaleFrameLayout != null) {
            View view = View.inflate(getActivity(), R.layout.view_content_unavailable, null);
            mProgressBarManager.hide();
            mScaleFrameLayout.addView(view);
        }
    }

    /**
     * Async class that will process everything for a given content list. This way, we
     * don't break on the user experience.
     */
    private class AsyncProcessAvContent extends AsyncTask<Void, Void, Boolean> {

        @Override
        public void onPreExecute() {
            long contentsSize = getContentPersistence().size(VodTvSectionFragment.this.getClass().getSimpleName());
            if (contentsSize > 0) {
                updateRowsAdapter();
            } else {
                mProgressBarManager.show();
            }
        }

        @Override
        public Boolean doInBackground(Void... params) {
            String avContentLink = getVodContentApiLink();

            try (InputStream catchupInputStream = RichFeedUtil.getInputStream(avContentLink)) {
                long persistedSize = getContentPersistence().size(VodTvSectionFragment.this.getClass().getSimpleName());

                if (!isCancelled()) {
                    final List<AvContent> avContents = AvContentUtil.getAvContentsList(catchupInputStream, VodTvSectionFragment.this.getClass().getSimpleName());
                    if (avContents.size() != persistedSize && persistedSize == 0) {
                        // Case where the content list is being loaded for the first time.
                        getContentPersistence().insert(avContents);
                    } else if (avContents.size() != persistedSize && avContents.size() != 0) {
                        // Case where the content list have been modified upstream.
                        getContentPersistence().deleteCategory(VodTvSectionFragment.this.getClass().getSimpleName());
                        getContentPersistence().insert(avContents);
                    } else if (persistedSize == 0) {
                        // Case where the upstream content list is empty and that there wasn't any content to begin with.
                        return (false);
                    }
                }
                return (true);
            } catch (IOException io) {
                return (false);
            }
        }

        @Override
        public void onPostExecute(Boolean result) {

             /*
              Only valid if it's the first time the database has been populated or if the content
              isn't available.
              */
            if (result && mRowsAdapter.size() == 0) {
                showRowsAdapter();
            } else if (result) {
                // Otherwise, update the adapter so it can be seamless to the user.
                updateRowsAdapter();
            } else {
                Log.e(LOG_TAG, "Couldn't parse contents");
                Log.e(LOG_TAG, "Api Link: " + getVodContentApiLink());
                showErrorView();
            }
        }
    }
}