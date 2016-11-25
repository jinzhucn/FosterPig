package com.minlu.fosterpig.activity;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.fragment.AllWarnFragment;

/**
 * Created by user on 2016/11/25.
 */
public class NotificationToWarnActivity extends BaseActivity {
    @Override
    public void onCreateContent() {

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_base_content,new AllWarnFragment()).commit();

    }
}
