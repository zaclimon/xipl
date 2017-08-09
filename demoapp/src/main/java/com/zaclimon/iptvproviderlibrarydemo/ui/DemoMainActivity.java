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

package com.zaclimon.iptvproviderlibrarydemo.ui;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.zaclimon.iptvproviderlibrary.util.ActivityUtil;
import com.zaclimon.iptvproviderlibrarydemo.DemoConstants;
import com.zaclimon.iptvproviderlibrarydemo.R;
import com.zaclimon.iptvproviderlibrarydemo.ui.urlinput.UrlInputActivity;

import java.net.URL;

/**
 * Demo Activity showcasing the Android IPTV Provider Library (aipl)
 *
 * @author zaclimon
 * Creation date: 09/08/17
 */

public class DemoMainActivity extends Activity {

    private final int URL_REQUEST_CODE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityUtil.isTvMode(this)) {
            Intent intent = new Intent(this, UrlInputActivity.class);
            startActivityForResult(intent, URL_REQUEST_CODE);
        } else {
            Toast.makeText(this, R.string.device_not_android_tv, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == URL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.tv_fragment_placeholder, new DemoMainFragment());
            transaction.commit();
        } else {
            finish();
        }

    }
}
