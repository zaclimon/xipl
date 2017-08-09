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

package com.zaclimon.iptvproviderlibrarydemo.ui.tvinputsetup;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.zaclimon.iptvproviderlibrarydemo.R;

/**
 * Demo Activity to showcase the configuration of a Tv input.
 *
 * @author zaclimon
 * Creation date: 09/08/17
 */

public class DemoTvInputSetupActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tv_input_setup);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.tv_input_setup_fragment_placeholder, new DemoTvInputSetupFragment());
        transaction.commit();
    }

}
