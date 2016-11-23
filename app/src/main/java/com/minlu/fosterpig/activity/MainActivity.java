package com.minlu.fosterpig.activity;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.customview.ColorfulRingProgressView;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ToastUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private FrameLayout mSafeNumber;
    private ColorfulRingProgressView mRingProgressView;
    private TextView mSafeProcessResult;
    private TextView mPercent;
    private TextView mAmmoniaMonitor;
    private TextView mTemperatureMonitor;
    private TextView mHumidityMonitor;
    private TextView mPowerSupplyMonitor;
    private TextView mAmmoniaWarn;
    private TextView mTemperatureWarn;
    private TextView mHumidityWarn;


    static class MyHandler extends Handler {
        WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mActivity.get();
            switch (msg.what) {
                case StringsFiled.SERVER_OUTAGE:
                    ToastUtil.showToast(mainActivity, "服务器宕机,请稍后");
                    mainActivity.setLoadingVisibility(View.GONE);
                    mainActivity.setIsInterruptTouch(false);
                    break;
                case StringsFiled.SERVER_THROW:
                    ToastUtil.showToast(mainActivity, "服务器正忙,请稍后");
                    mainActivity.setLoadingVisibility(View.GONE);
                    mainActivity.setIsInterruptTouch(false);
                    break;
                case StringsFiled.SERVER_SEND_JSON:
                    mainActivity.analysisDataJSON();
                    break;
            }
        }
    }

    MyHandler myHandler = new MyHandler(this);

    @Override
    public void onCreateContent() {
        View view = setContent(R.layout.activity_main);

        initContentView(view);
    }

    private void initContentView(View view) {

        getThreePoint().setOnClickListener(this);
        getThreeLine().setOnClickListener(this);

        // 四个条目中代表监控数的文本
        mAmmoniaMonitor = (TextView) view.findViewById(R.id.tv_item_ammonia_monitor_number);
        mTemperatureMonitor = (TextView) view.findViewById(R.id.tv_item_temperature_monitor_number);
        mHumidityMonitor = (TextView) view.findViewById(R.id.tv_item_humidity_monitor_number);
        mPowerSupplyMonitor = (TextView) view.findViewById(R.id.tv_item_power_supply_monitor_number);

        // 三个条目中代表警报数的文本
        mAmmoniaWarn = (TextView) view.findViewById(R.id.tv_item_ammonia_warn_number);
        mTemperatureWarn = (TextView) view.findViewById(R.id.tv_item_temperature_warn_number);
        mHumidityWarn = (TextView) view.findViewById(R.id.tv_item_humidity_warn_number);

        // 四个条目的点击事件
        RelativeLayout mItemAmmonia = (RelativeLayout) view.findViewById(R.id.rl_item_ammonia);
        mItemAmmonia.setOnClickListener(this);
        RelativeLayout mItemTemperature = (RelativeLayout) view.findViewById(R.id.rl_item_temperature);
        mItemTemperature.setOnClickListener(this);
        RelativeLayout mItemPowerSupply = (RelativeLayout) view.findViewById(R.id.rl_item_power_supply);
        mItemPowerSupply.setOnClickListener(this);
        RelativeLayout mItemHumidity = (RelativeLayout) view.findViewById(R.id.rl_item_humidity);
        mItemHumidity.setOnClickListener(this);

        // 圆环扫描相关控件
        mSafeProcessResult = (TextView) view.findViewById(R.id.tv_safe_process_result);
        mPercent = (TextView) view.findViewById(R.id.tv_percent);
        mSafeNumber = (FrameLayout) view.findViewById(R.id.fl_safe_number_annulus);
        mRingProgressView = (ColorfulRingProgressView) view.findViewById(R.id.color_ful_ring_progress_view);
        mRingProgressView.setOnClickListener(this);

        if (mSafeNumber != null && mRingProgressView != null) {
            ViewTreeObserver viewTreeObserver = mSafeNumber.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mSafeNumber.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    ViewGroup.LayoutParams layoutParams = mRingProgressView.getLayoutParams();
                    layoutParams.width = mSafeNumber.getHeight();
                    mRingProgressView.setLayoutParams(layoutParams);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_item_ammonia:
                if (mAmmoniaWarn.getVisibility() == View.VISIBLE) {
                    mainSkipToWarn(StringsFiled.MAIN_TO_WARN_AMMONIA);
                }
                break;
            case R.id.rl_item_temperature:
                if (mTemperatureWarn.getVisibility() == View.VISIBLE) {
                    mainSkipToWarn(StringsFiled.MAIN_TO_WARN_TEMPERATURE);
                }
                break;
            case R.id.rl_item_humidity:
                if (mHumidityWarn.getVisibility() == View.VISIBLE) {
                    mainSkipToWarn(StringsFiled.MAIN_TO_WARN_HUMIDITY);
                }
                break;
            case R.id.rl_item_power_supply:
                break;
            case R.id.color_ful_ring_progress_view:

                setLoadingVisibility(View.VISIBLE);
                setIsInterruptTouch(true);
                // 点击圆环开始请求网络
                requestAllMonitorInformation();

                break;
            case R.id.iv_title_three_line:
                Intent trueTimeIntent = new Intent(getApplicationContext(), TrueTimeDataActivity.class);
                trueTimeIntent.putExtra(StringsFiled.ACTIVITY_TITLE, "实时数据");
                startActivity(trueTimeIntent);
                break;
            case R.id.iv_title_three_point:
                Intent settingIntent = new Intent(getApplicationContext(), SettingActivity.class);
                settingIntent.putExtra(StringsFiled.ACTIVITY_TITLE, "设置");
                startActivity(settingIntent);
                break;
        }


    }

    private void mainSkipToWarn(String mainToWarn) {
        Intent intent = new Intent(getApplicationContext(), WarnActivity.class);
        intent.putExtra(StringsFiled.MAIN_TO_WARN, mainToWarn);
        intent.putExtra(StringsFiled.ACTIVITY_TITLE, "报警信息");
        startActivity(intent);
    }

    private void requestAllMonitorInformation() {

        OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
        RequestBody formBody = new FormBody.Builder().build();
        String address = SharedPreferencesUtil.getString(
                ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");
        Request request = new Request.Builder()
                .url("https://www.baidu.com/")
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                myHandler.sendEmptyMessage(StringsFiled.SERVER_OUTAGE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (StringUtils.interentIsNormal(response.body().string())) {
                    myHandler.sendEmptyMessage(StringsFiled.SERVER_SEND_JSON);
                } else {
                    myHandler.sendEmptyMessage(StringsFiled.SERVER_THROW);
                }
            }
        });

    }

    private void analysisDataJSON() {
        // 解析json数据
        System.out.println("解析数据");

        mAmmoniaWarn.setVisibility(View.VISIBLE);
        mHumidityWarn.setVisibility(View.VISIBLE);
        mTemperatureWarn.setVisibility(View.VISIBLE);

        setLoadingVisibility(View.GONE);
        setIsInterruptTouch(false);

    }
}
