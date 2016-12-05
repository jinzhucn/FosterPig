package com.minlu.fosterpig.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.base.MyApplication;
import com.minlu.fosterpig.bean.MainAllInformation;
import com.minlu.fosterpig.customview.ColorfulRingProgressView;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.observer.MySubject;
import com.minlu.fosterpig.observer.Observers;
import com.minlu.fosterpig.util.GsonTools;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ToastUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener, Observers {

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
    private TextView mPowerSupplyWarn;
    private String mResultJSON;

    private float mAllFacilityData = 0f;
    private float mAllWarnFacilityData = 0f;
    private int mSafePercentNumber = 100;

    private boolean singleIsWarn = false;// 使用它的地方已经注释
    private int mAmmoniaAllNumber = 0;
    private int mTemperatureAllNumber = 0;
    private int mHumidityAllNumber = 0;
    private int mPowerSupplyAllNumber = 0;
    private int mAmmoniaWarnNumber = 0;
    private int mTemperatureWarnNumber = 0;
    private int mHumidityWarnNumber = 0;
    private int mPowerSupplyWarnNumber = 0;
    private String mAreaName;
    private String mSiteName;
    private String mFacilityName;

    private List<MainAllInformation> mAllAmmoniaWarnData = new ArrayList<>();
    private List<MainAllInformation> mAllTemperatureWarnData = new ArrayList<>();
    private List<MainAllInformation> mAllHumidityWarnData = new ArrayList<>();
    private List<MainAllInformation> mAllPowerSupplyWarnData = new ArrayList<>();

    private final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE};

    private static int sizeOfInt(int x) {
        for (int i = 0; ; i++)
            if (x <= sizeTable[i])
                return i + 1;
    }

    @Override
    public void update(int distinguishNotified, int position, int cancelOrderBid) {
        switch (distinguishNotified) {
            case StringsFiled.OBSERVER_AMMONIA_SURE:
                mAllWarnFacilityData--;

                countGetSafeValue();// 这个是下面两个方法的前提

                updateRingProgress();
                gistSafeNumberSetText();

                mAmmoniaWarnNumber--;
                updateFourItem();
                break;
            case StringsFiled.OBSERVER_TEMPERATURE_SURE:
                mAllWarnFacilityData--;
                countGetSafeValue();
                updateRingProgress();
                gistSafeNumberSetText();

                mTemperatureWarnNumber--;
                updateFourItem();
                break;
            case StringsFiled.OBSERVER_HUMIDITY_SURE:
                mAllWarnFacilityData--;
                countGetSafeValue();
                updateRingProgress();
                gistSafeNumberSetText();

                mHumidityWarnNumber--;
                updateFourItem();
                break;

            case StringsFiled.OBSERVER_POWER_SUPPLY_SURE:
                mAllWarnFacilityData--;
                countGetSafeValue();
                updateRingProgress();
                gistSafeNumberSetText();

                mPowerSupplyWarnNumber--;
                updateFourItem();
                break;
        }
    }

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
                case StringsFiled.STOP_LOADING_BUT_NO_CLICK:// 在这里是获取到了具体数据，在进行解析前先停止加载页面，并初始化一些数据与Ui
                    mainActivity.setLoadingVisibility(View.GONE);
                    mainActivity.dataInitToView();
                    break;
                case StringsFiled.MAIN_ANALYSIS_FINISH_JSON:
                    mainActivity.mSafeProcessResult.setTextColor(ContextCompat.getColor(mainActivity, R.color.white));
                    mainActivity.gistSafeNumberSetText();
                    mainActivity.setIsInterruptTouch(false);
                    ToastUtil.showToast(mainActivity, "扫描完成");
                    break;
                case StringsFiled.MAIN_DISPOSE_DATA_TO_UI:
                    mainActivity.updateRingProgress();
                    mainActivity.updateSafeProcessResult();
                    mainActivity.setFourItemAllNumber();
                    mainActivity.updateFourItem();
                    break;
            }
        }
    }

    private void gistSafeNumberSetText() {
        if (mSafePercentNumber == 100) {
            mSafeProcessResult.setText("您的系统很安全,点击安全指数重新检测");
        } else if (mSafePercentNumber >= 80 && mSafePercentNumber < 100) {
            mSafeProcessResult.setText("安全等级良好,点击安全指数重新检测");
        } else if (mSafePercentNumber < 80 && mSafePercentNumber >= 60) {
            mSafeProcessResult.setText("安全等级及格,点击安全指数重新检测");
        } else if (mSafePercentNumber < 60) {
            mSafeProcessResult.setText("安全等级不及格,点击安全指数重新检测");
        }
    }

    MyHandler myHandler = new MyHandler(this);

    @Override
    public void onCreateContent() {
        View view = setContent(R.layout.activity_main);

        MySubject.getInstance().add(this);

        boolean informWarn = SharedPreferencesUtil.getboolean(
                ViewsUitls.getContext(), StringsFiled.INFORM_WARN, false);
        if (informWarn) {
            if (!ViewsUitls.isServiceWork("com.minlu.fosterpig.activity.AlarmServicer")) {
                System.out.println("=====================在主界面开启了报警服务===============");
                startService(MyApplication.getIntentServicer());
            }
        }

        MyApplication.getSaveActivity().add(this);

        initContentView(view);

//        startRunPoint();
    }

    private void initContentView(View view) {

        getThreePoint().setOnClickListener(this);
        getThreeLine().setOnClickListener(this);

        // 四个条目中代表监控数的文本
        mAmmoniaMonitor = (TextView) view.findViewById(R.id.tv_item_ammonia_monitor_number);
        mTemperatureMonitor = (TextView) view.findViewById(R.id.tv_item_temperature_monitor_number);
        mHumidityMonitor = (TextView) view.findViewById(R.id.tv_item_humidity_monitor_number);
        mPowerSupplyMonitor = (TextView) view.findViewById(R.id.tv_item_power_supply_monitor_number);

        // 四个条目中代表警报数的文本
        mAmmoniaWarn = (TextView) view.findViewById(R.id.tv_item_ammonia_warn_number);
        mTemperatureWarn = (TextView) view.findViewById(R.id.tv_item_temperature_warn_number);
        mHumidityWarn = (TextView) view.findViewById(R.id.tv_item_humidity_warn_number);
        mPowerSupplyWarn = (TextView) view.findViewById(R.id.tv_item_power_supply_warn_number);

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
                if (mPowerSupplyWarnNumber > 0) {
                    mainSkipToWarn(StringsFiled.MAIN_TO_WARN_POWER_SUPPLY);
                }
                break;
            case R.id.color_ful_ring_progress_view:
                startRunPoint();
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

    private void startRunPoint() {
        setLoadingVisibility(View.VISIBLE);
        setIsInterruptTouch(true);
        // 点击圆环开始请求网络
        requestAllMonitorInformation();
    }

    private void mainSkipToWarn(String mainToWarn) {
        Intent intent = new Intent(getApplicationContext(), WarnActivity.class);
        intent.putExtra(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY, mainToWarn);
        intent.putExtra(StringsFiled.ACTIVITY_TITLE, "报警信息");
        startActivity(intent);
    }

    private void requestAllMonitorInformation() {

        OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
        RequestBody formBody = new FormBody.Builder().build();
        String address = SharedPreferencesUtil.getString(
                ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");
        Request request = new Request.Builder()
                .url(address + IpFiled.MAIN_GET_ALL_INFORMATION)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                myHandler.sendEmptyMessage(StringsFiled.SERVER_OUTAGE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mResultJSON = response.body().string();
                if (StringUtils.interentIsNormal(mResultJSON)) {
                    analysisDataJSON();
                } else {
                    myHandler.sendEmptyMessage(StringsFiled.SERVER_THROW);
                }
            }
        });

    }

    private void analysisDataJSON() {

        // TODO 测试数据
  /*      try {
            InputStream is = getAssets().open("textJson.txt");
            mResultJSON = readTextFromSDcard(is);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        // TODO 测试数据
        // 解析json数据
        System.out.println("解析数据: " + mResultJSON);
        try {
            JSONObject jsonObject = new JSONObject(mResultJSON);
            JSONObject allInformation = jsonObject.optJSONObject("mapList");
            if (allInformation.has("selectList")) {
                JSONArray informationList = allInformation.optJSONArray("selectList");
                if (informationList.length() > 0) {

                    dataInit();

                    // 这里是准备分析一条条数据，所以去除转圈，但不能点击
                    myHandler.sendEmptyMessage(StringsFiled.STOP_LOADING_BUT_NO_CLICK);

                    mAllFacilityData = informationList.length();// 每次准备解析数组数据，就将数组的长度赋值给mAllFacilityData
                    mAllWarnFacilityData = 0f;// 每次准备解析数组数据，就将报警设备数清零
                    for (int i = 0; i < informationList.length(); i++) {

                        JSONObject singleInformation = informationList.getJSONObject(i);

                        int facilityType = singleInformation.optInt("type");//1氨气 2温度 3湿度 4市电通道一 。。。11市电通道八
                        double facilityValue = singleInformation.optDouble("value");// 市电的值0断1通  温湿氨气为double

                        // 获取是否报警
                        int isWarn = -1;
                        if (singleInformation.has("police")) {
                            isWarn = singleInformation.optInt("police");// 1报警0不报警 市电没有这个字段
                        }

                        String startWarnTime = "---";
                        if (singleInformation.has("startWarnTime")) {
                            startWarnTime = singleInformation.optString("startWarnTime");
                        }
                        String siteName = singleInformation.optString("dtuName");
                        String facilityName = singleInformation.optString("lmuName");
                        String areaName = singleInformation.optString("stationName");
                        int siteId = singleInformation.optInt("dtuId");
                        int facilityId = singleInformation.optInt("lmuId");
                        int areaId = singleInformation.optInt("stationId");

                        int mainId = -1;
                        if (singleInformation.has("id")) {
                            mainId = singleInformation.optInt("id");
                        }
                        // 下面是对数据进行处理，发到ui进行更新
                        disposeDataToUI(mainId, facilityType, facilityValue, isWarn, siteName, areaName, facilityName, siteId, facilityId, areaId, i, startWarnTime);

                        // 延迟时间，给ui更新
                        try {
                            Thread.sleep((int) (mAllFacilityData * 20) / informationList.length());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        myHandler.sendEmptyMessage(StringsFiled.MAIN_DISPOSE_DATA_TO_UI);
                    }

                    System.out.println();
                    SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_AMMONIA_JSON, GsonTools.createGsonString(mAllAmmoniaWarnData));
                    SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_TEMPERATURE_JSON, GsonTools.createGsonString(mAllTemperatureWarnData));
                    SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_HUMIDITY_JSON, GsonTools.createGsonString(mAllHumidityWarnData));
                    SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_POWER_SUPPLY_JSON, GsonTools.createGsonString(mAllPowerSupplyWarnData));

                    myHandler.sendEmptyMessage(StringsFiled.MAIN_ANALYSIS_FINISH_JSON);
                } else {
                    // json数组里没有一点数据说明服务器异常了
                    myHandler.sendEmptyMessage(StringsFiled.SERVER_THROW);
                }
            } else {
                // 没有selectList这个字段说明服务器异常了
                myHandler.sendEmptyMessage(StringsFiled.SERVER_THROW);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void disposeDataToUI(int mainId, int facilityType, double facilityValue, int isWarn, String siteName,
                                 String areaName, String facilityName, int siteId, int facilityId, int areaId, int i, String startWarnTime) {
        MainAllInformation mainAllInformation = new MainAllInformation(mainId, areaName, siteName, siteId, facilityName, facilityId, areaId, facilityType, facilityValue, isWarn, startWarnTime);
        mAreaName = areaName;
        mSiteName = siteName;
        switch (facilityType) {
            case 1:// 1氨气 a
                mFacilityName = "氨气传感器";
                mAmmoniaAllNumber++;
                if (isWarn == 1) {
                    mAllWarnFacilityData++; // 温湿氨的报警
                    mAmmoniaWarnNumber++;
                    singleIsWarn = true;
                    mAllAmmoniaWarnData.add(mainAllInformation);
                } else {
                    singleIsWarn = false;
                }
                break;
            case 2:// 2温度 t
                mFacilityName = "温度传感器";
                mTemperatureAllNumber++;
                if (isWarn == 1) {
                    mAllWarnFacilityData++; // 温湿氨的报警
                    mTemperatureWarnNumber++;
                    singleIsWarn = true;
                    mAllTemperatureWarnData.add(mainAllInformation);
                } else {
                    singleIsWarn = false;
                }
                break;
            case 3:// 3湿度 h
                mFacilityName = "湿度传感器";
                mHumidityAllNumber++;
                if (isWarn == 1) {
                    mAllWarnFacilityData++; // 温湿氨的报警
                    mHumidityWarnNumber++;
                    singleIsWarn = true;
                    mAllHumidityWarnData.add(mainAllInformation);
                } else {
                    singleIsWarn = false;
                }
                break;
            default:// 市电 p
                mFacilityName = "市电通道" + (facilityType - 3);
                mPowerSupplyAllNumber++;
                if (isWarn == 1) {
                    mAllWarnFacilityData++; // 市电的报警
                    mPowerSupplyWarnNumber++;
                    singleIsWarn = true;
                    mAllPowerSupplyWarnData.add(mainAllInformation);
                } else {
                    singleIsWarn = false;
                }
                break;
        }
        // 计算获取安全数值
        countGetSafeValue();

        // Log.v("allData", "安全指数：" + mSafePercentNumber + " 氨气总数：" + mAmmoniaAllNumber + " 温度总数：" + mTemperatureAllNumber + " 湿度总数：" + mHumidityAllNumber + " 市电总数：" + mPowerSupplyAllNumber + " 氨气报警总数：" + mAmmoniaWarnNumber + " 温度报警总数：" + mTemperatureWarnNumber + " 湿度报警总数：" + mHumidityWarnNumber + " 市电报警总数：" + mPowerSupplyWarnNumber + " 区域名字：" + mAreaName + " 站点名字：" + mSiteName + " 设备名字：" + mFacilityName);
    }

    private void countGetSafeValue() {
        mSafePercentNumber = (int) (((mAllFacilityData - mAllWarnFacilityData) / mAllFacilityData) * 100);
    }

    private void dataInitToView() {
        setFourItemAllNumber();
        updateFourItem();
        updateRingProgress();
    }

    private void dataInit() {
        mAmmoniaAllNumber = 0;
        mTemperatureAllNumber = 0;
        mHumidityAllNumber = 0;
        mPowerSupplyAllNumber = 0;

        mAmmoniaWarnNumber = 0;
        mTemperatureWarnNumber = 0;
        mHumidityWarnNumber = 0;
        mPowerSupplyWarnNumber = 0;

        singleIsWarn = false;
        mFacilityName = "";
        mAreaName = "";
        mSiteName = "";

        mSafePercentNumber = 100;

        // 存储四大模块的数据
        mAllAmmoniaWarnData.clear();
        mAllTemperatureWarnData.clear();
        mAllHumidityWarnData.clear();
        mAllPowerSupplyWarnData.clear();
    }

    private String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        return buffer.toString();
    }

    private void updateFourItem() {
        // 设置四个模块的警告红点
        if (mAmmoniaWarnNumber > 0) {
            mAmmoniaWarn.setVisibility(View.VISIBLE);
            mAmmoniaWarn.setText("" + mAmmoniaWarnNumber);
        } else {
            mAmmoniaWarn.setVisibility(View.INVISIBLE);
        }
        if (mTemperatureWarnNumber > 0) {
            mTemperatureWarn.setVisibility(View.VISIBLE);
            mTemperatureWarn.setText("" + mTemperatureWarnNumber);
        } else {
            mTemperatureWarn.setVisibility(View.INVISIBLE);
        }
        if (mHumidityWarnNumber > 0) {
            mHumidityWarn.setVisibility(View.VISIBLE);
            mHumidityWarn.setText("" + mHumidityWarnNumber);
        } else {
            mHumidityWarn.setVisibility(View.INVISIBLE);
        }
        if (mPowerSupplyWarnNumber > 0) {
            mPowerSupplyWarn.setVisibility(View.VISIBLE);
            mPowerSupplyWarn.setText("" + mPowerSupplyWarnNumber);
        } else {
            mPowerSupplyWarn.setVisibility(View.INVISIBLE);
        }
    }

    private void setFourItemAllNumber() {
        // 设置四个模块下的文本
        mAmmoniaMonitor.setText("氨气[" + mAmmoniaAllNumber + "]");
        mHumidityMonitor.setText("湿度[" + mHumidityAllNumber + "]");
        mTemperatureMonitor.setText("温度[" + mTemperatureAllNumber + "]");
        mPowerSupplyMonitor.setText("市电[" + mPowerSupplyAllNumber + "]");// mPowerSupplyWarnNumber + "/" +

//        setPowerSupplyMonitorTextColor();
    }

    private void setPowerSupplyMonitorTextColor() {
        SpannableStringBuilder builder = new SpannableStringBuilder(mPowerSupplyMonitor.getText().toString());
        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
        builder.setSpan(redSpan, 3, MainActivity.sizeOfInt(mPowerSupplyWarnNumber) + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPowerSupplyMonitor.setText(builder);
    }

    private void updateSafeProcessResult() {
        // 设置圆环下的文本内容与颜色
        mSafeProcessResult.setText(mAreaName + "-" + mSiteName + "-" + mFacilityName);
       /* if (singleIsWarn) {
            mSafeProcessResult.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.red));
        } else {
            mSafeProcessResult.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        }*/
    }

    /*根据mSafePercentNumber值修改控件ui*/
    private void updateRingProgress() {
        // 更新圆环进度与颜色，安全指数大小与颜色
        mRingProgressView.setPercent(mSafePercentNumber);
        mPercent.setText(mSafePercentNumber + "分");
        if (mSafePercentNumber == 100) {
            mRingProgressView.setFgColorEnd(ContextCompat.getColor(MainActivity.this, R.color.white));
            mRingProgressView.setFgColorStart(ContextCompat.getColor(MainActivity.this, R.color.white));
            mPercent.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        }
        if (mSafePercentNumber >= 80 && mSafePercentNumber < 100) {
            mRingProgressView.setFgColorEnd(ContextCompat.getColor(MainActivity.this, R.color.liang_hao));
            mRingProgressView.setFgColorStart(ContextCompat.getColor(MainActivity.this, R.color.liang_hao));
            mPercent.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.liang_hao));
        } else if (mSafePercentNumber < 80 && mSafePercentNumber >= 60) {
            mRingProgressView.setFgColorEnd(ContextCompat.getColor(MainActivity.this, R.color.ji_ge));
            mRingProgressView.setFgColorStart(ContextCompat.getColor(MainActivity.this, R.color.ji_ge));
            mPercent.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.ji_ge));
        } else if (mSafePercentNumber < 60) {
            mRingProgressView.setFgColorEnd(ContextCompat.getColor(MainActivity.this, R.color.red));
            mRingProgressView.setFgColorStart(ContextCompat.getColor(MainActivity.this, R.color.red));
            mPercent.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.red));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MySubject.getInstance().del(this);
        MyApplication.getSaveActivity().remove(this);
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {                                                    //两次按键小于2秒时，退出应用
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
