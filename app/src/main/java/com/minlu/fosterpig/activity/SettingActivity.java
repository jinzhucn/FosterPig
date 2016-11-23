package com.minlu.fosterpig.activity;

import android.view.View;

import com.minlu.fosterpig.base.BaseActivity;


/**
 * Created by user on 2016/11/23.
 */
public class SettingActivity extends BaseActivity {
    @Override
    public void onCreateContent() {

        getThreeLine().setVisibility(View.GONE);
        setBackVisibility(View.VISIBLE);
        setSettingVisibility(View.GONE);

    }
}
