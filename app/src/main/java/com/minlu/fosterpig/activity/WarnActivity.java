package com.minlu.fosterpig.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.fragment.MainToWarnFragment;


/**
 * Created by user on 2016/11/22.
 */
public class WarnActivity extends BaseActivity {
    @Override
    public void onCreateContent() {

        getThreeLine().setVisibility(View.GONE);
        setBackVisibility(View.VISIBLE);
        setSettingVisibility(View.GONE);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        switch (getIntent().getStringExtra(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY)) {
            case StringsFiled.MAIN_TO_WARN_AMMONIA:
                bundle.putInt(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY, StringsFiled.MAIN_TO_WARN_VALUE_AMMONIA);
                break;
            case StringsFiled.MAIN_TO_WARN_TEMPERATURE:
                bundle.putInt(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY, StringsFiled.MAIN_TO_WARN_VALUE_TEMPERATURE);
                break;
            case StringsFiled.MAIN_TO_WARN_HUMIDITY:
                bundle.putInt(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY, StringsFiled.MAIN_TO_WARN_VALUE_HUMIDITY);
                break;
        }
        MainToWarnFragment mainToWarnFragment = new MainToWarnFragment();
        mainToWarnFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_base_content, mainToWarnFragment);
        fragmentTransaction.commit();
    }
}
