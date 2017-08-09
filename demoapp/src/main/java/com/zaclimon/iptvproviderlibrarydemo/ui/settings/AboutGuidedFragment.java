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

package com.zaclimon.iptvproviderlibrarydemo.ui.settings;

import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;


import com.zaclimon.iptvproviderlibrarydemo.R;

import java.util.List;

/**
 * Fragment that shows the about section of the app the user information.
 *
 * @author zaclimon
 * Creation date: 03/07/17
 */

public class AboutGuidedFragment extends GuidedStepFragment {

    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {

        String title = getString(R.string.about_title);
        String description = getString(R.string.about_description);

        return (new GuidanceStylist.Guidance(title, description, null, null));
    }

    @Override
    public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
        GuidedAction.Builder okAction = new GuidedAction.Builder(getActivity());
        okAction.clickAction(GuidedAction.ACTION_ID_OK);
        actions.add(okAction.build());
    }

    @Override
    public void onGuidedActionClicked(GuidedAction guidedAction) {
        getActivity().finish();
    }
}