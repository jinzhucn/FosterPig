package com.minlu.fosterpig.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.hikvision.sdk.net.bean.Camera;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;

/**
 * Created by user on 2017/1/18.
 */
public class LiveActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_screen);


        Intent intent = getIntent();
        Camera camera = (Camera) intent.getSerializableExtra(StringsFiled.CAMERA_INFORMATION);







    }
}
