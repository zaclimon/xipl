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

package com.zaclimon.xipl.ui.components.cardview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.leanback.widget.BaseCardView;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zaclimon.xipl.R;
import com.zaclimon.xipl.model.AvContent;
import com.zaclimon.xipl.ui.settings.ProviderSettingsObjectAdapter;

/**
 * Custom {@link Presenter} class that is used to show {@link ImageCardView}
 * in a given list. (Mostly Leanback related UI widgets)
 *
 * @author zaclimon
 * Creation date: 21/06/17
 */

public class CardViewPresenter extends Presenter {

    private CardViewImageProcessor mCardViewImageProcessor;

    /**
     * Default constructor
     */
    public CardViewPresenter() {
        mCardViewImageProcessor = null;
    }

    /**
     * Additional constructor if processing an image from an external resource is required
     *
     * @param cardViewImageProcessor the processor which will be used to retrieve an image.
     */
    public CardViewPresenter(CardViewImageProcessor cardViewImageProcessor) {
        mCardViewImageProcessor = cardViewImageProcessor;
    }

    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ImageCardView imageCardView = new ImageCardView(parent.getContext());

        int widthPixels = (int) parent.getResources().getDimension(R.dimen.cardview_presenter_width);
        int heightPixels = (int) parent.getResources().getDimension(R.dimen.cardview_presenter_height);

        imageCardView.setInfoVisibility(BaseCardView.CARD_REGION_VISIBLE_ALWAYS);
        imageCardView.setFocusable(true);
        imageCardView.setFocusableInTouchMode(true);
        imageCardView.setMainImageDimensions(widthPixels, heightPixels);
        return (new ViewHolder(imageCardView));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        final ImageCardView imageCardView = (ImageCardView) viewHolder.view;
        Context context = viewHolder.view.getContext();

        int widthPixels = (int) viewHolder.view.getResources().getDimension(R.dimen.cardview_presenter_width);
        int heightPixels = (int) viewHolder.view.getResources().getDimension(R.dimen.cardview_presenter_height);

        if (item instanceof Bundle) {
            // We're dealing with a Settings menu value
            Bundle settingsBundle = (Bundle) item;
            String name = context.getString(settingsBundle.getInt(ProviderSettingsObjectAdapter.BUNDLE_SETTINGS_NAME_ID));
            Drawable drawable = context.getDrawable(settingsBundle.getInt(ProviderSettingsObjectAdapter.BUNDLE_SETTINGS_DRAWABLE_ID));
            imageCardView.setTitleText(name);
            imageCardView.setMainImage(drawable);
        } else if (item instanceof AvContent) {
            // We're dealing with an AvContent item (TvCatchup/VOD)
            final AvContent avContent = (AvContent) item;
            imageCardView.setTitleText(avContent.getTitle());

            viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(view.getContext(), avContent.getTitle(), Toast.LENGTH_SHORT).show();
                    return (true);
                }
            });

            if (!TextUtils.isEmpty(avContent.getLogo()) && mCardViewImageProcessor != null) {
                mCardViewImageProcessor.loadImage(avContent.getLogo(), widthPixels, heightPixels, imageCardView.getMainImageView());
            }
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}