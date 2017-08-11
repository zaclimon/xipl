package com.zaclimon.iptvproviderlibrary.ui.vod;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.app.ProgressBarManager;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.ScaleFrameLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.zaclimon.iptvproviderlibrary.R;
import com.zaclimon.iptvproviderlibrary.model.AvContent;
import com.zaclimon.iptvproviderlibrary.persistence.ContentPersistence;
import com.zaclimon.iptvproviderlibrary.ui.components.cardview.CardViewImageProcessor;
import com.zaclimon.iptvproviderlibrary.ui.components.cardview.CardViewPresenter;
import com.zaclimon.iptvproviderlibrary.util.AvContentUtil;
import com.zaclimon.iptvproviderlibrary.util.RichFeedUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class in which VOD-like (Video on demand) fragments can base off in order to have a complete
 * list of content based on their provider's catalog.
 *
 * @author zaclimon
 * Creation date: 05/07/17
 */

public abstract class VodTvSectionFragment extends RowsFragment {

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
    private List<AvContent> mModifiedContents;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        mProgressBarManager = ((BrowseFragment) getParentFragment()).getProgressBarManager();
        mScaleFrameLayout = getActivity().findViewById(R.id.scale_frame);
        mProgressBarManager.setRootView((ViewGroup) getActivity().findViewById(R.id.browse_container_dock));
        setOnItemViewClickedListener(new AvContentTvItemClickListener());
        setAdapter(mRowsAdapter);

        mAsyncProcessAvContent = new AsyncProcessAvContent();
        mAsyncProcessAvContent.execute();

    }

    /**
     * Updates the main RowAdapter of the Fragment.
     */
    private void updateRowsAdapter() {

        final List<String> avGroups = new ArrayList<>();
        final List<ArrayObjectAdapter> avAdapters = new ArrayList<>();
        List<AvContent> contents = getContentPersistence().searchCategory(VodTvSectionFragment.this.getClass().getSimpleName(), true);

        mProgressBarManager.hide();

        if (!contents.isEmpty()) {
            String currentGroup = contents.get(0).getGroup();
            ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new CardViewPresenter(getImageProcessor()));
            avGroups.add(currentGroup);

            for (AvContent content : contents) {
                if (!currentGroup.equals(content.getGroup())) {
                    avAdapters.add(arrayObjectAdapter);
                    avGroups.add(content.getGroup());
                    arrayObjectAdapter = new ArrayObjectAdapter(new CardViewPresenter(getImageProcessor()));
                    currentGroup = content.getGroup();
                }
                arrayObjectAdapter.add(content);
            }

            if (mRowsAdapter.size() == 0) {
                for (int i = 0; i < avAdapters.size(); i++) {
                    HeaderItem catchupItem = new HeaderItem(avGroups.get(i));
                    mRowsAdapter.add(new ListRow(catchupItem, avAdapters.get(i)));
                }
            }
        } else if (mScaleFrameLayout != null) {
            showErrorView();
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mModifiedContents != null) {
            getContentPersistence().deleteCategory(VodTvSectionFragment.this.getClass().getSimpleName());
            getContentPersistence().insert(mModifiedContents);
        }
    }

    /**
     * Shows a view in which content is not available and hides the progress bar if it was shown.
     */
    private void showErrorView() {
        if (isAdded()) {
            View view = View.inflate(getActivity(), R.layout.view_content_unavailable, null);
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
                        getContentPersistence().insert(avContents);
                    } else if (avContents.size() != persistedSize) {
                        mModifiedContents = avContents;
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
                updateRowsAdapter();
            } else if (!result) {
                Log.e(LOG_TAG, "Couldn't parse contents");
                Log.e(LOG_TAG, "Api Link: " + getVodContentApiLink());
                showErrorView();
            }
        }
    }

    /**
     * Class acting as a onItemViewClickedListener to play an {@link AvContent}
     *
     * @author zaclimon
     * Creation date: 02/07/17
     */
    private class AvContentTvItemClickListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof AvContent) {
                // The item comes from an AvContent element.
                AvContent avContent = (AvContent) item;
                Intent intent = new Intent(getActivity(), VodPlaybackActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(AV_CONTENT_TITLE_BUNDLE, avContent.getTitle());
                bundle.putString(AV_CONTENT_LOGO_BUNDLE, avContent.getLogo());
                bundle.putString(AV_CONTENT_LINK_BUNDLE, avContent.getContentLink());
                bundle.putString(AV_CONTENT_GROUP_BUNDLE, avContent.getGroup());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }
}