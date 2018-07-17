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

package com.zaclimon.xipl.ui.components.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.zaclimon.xipl.model.AvContent;
import com.zaclimon.xipl.ui.vod.VodPlaybackActivity;
import com.zaclimon.xipl.ui.vod.VodTvSectionFragment;

/**
 * Class acting as a onItemViewClickedListener to play an {@link AvContent}
 *
 * @author zaclimon
 * Creation date: 02/07/17
 */

public class AvContentTvItemClickListener implements OnItemViewClickedListener {

    private Class<? extends VodPlaybackActivity> mClass;

    /**
     * Default constructor
     *
     * @param aClass the {@link VodPlaybackActivity} based class that will be used for playback.
     */
    public AvContentTvItemClickListener(Class<? extends VodPlaybackActivity> aClass) {
        mClass = aClass;
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

        if (item instanceof AvContent) {
            // The item comes from an AvContent element. Get the context from it's ViewHolder.
            AvContent avContent = (AvContent) item;
            Context context = itemViewHolder.view.getContext();
            Intent intent = new Intent(context, mClass);
            Bundle bundle = new Bundle();
            bundle.putString(VodTvSectionFragment.AV_CONTENT_TITLE_BUNDLE, avContent.getTitle());
            bundle.putString(VodTvSectionFragment.AV_CONTENT_LOGO_BUNDLE, avContent.getLogo());
            bundle.putString(VodTvSectionFragment.AV_CONTENT_LINK_BUNDLE, avContent.getContentLink());
            bundle.putString(VodTvSectionFragment.AV_CONTENT_GROUP_BUNDLE, avContent.getGroup());
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

}
