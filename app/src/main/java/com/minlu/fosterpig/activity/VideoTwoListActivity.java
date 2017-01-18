package com.minlu.fosterpig.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.fragment.VideoListFragment;

/**
 * Created by user on 2017/1/18.
 */

public class VideoTwoListActivity extends BaseActivity {
    @Override
    public void onCreateContent() {
        getThreeLine().setVisibility(View.GONE);
        setBackVisibility(View.VISIBLE);
        setSettingVisibility(View.GONE);

        Intent intent = getIntent();

        Bundle bundle = new Bundle();
        bundle.putBoolean(StringsFiled.GET_TWO_LIST_VIEW, true);
        bundle.putInt(StringsFiled.PARENT_NODE_TYPE, intent.getIntExtra(StringsFiled.PARENT_NODE_TYPE, -1));
        bundle.putInt(StringsFiled.PARENT_ID, intent.getIntExtra(StringsFiled.PARENT_ID, -1));

        VideoListFragment videoListFragment = new VideoListFragment();
        videoListFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_base_content, videoListFragment).commit();
    }
}
