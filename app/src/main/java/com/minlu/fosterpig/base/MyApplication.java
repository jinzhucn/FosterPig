package com.minlu.fosterpig.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class MyApplication extends Application {
    private static Context mContext;
    private static long mainThreadId;
    private static MyApplication ins;
    private static Handler mHandler;

    @Override
    public void onCreate() {
        // 在onCreate方法中初始化公共变量，Context，Handler，main Thread id
        super.onCreate();

        mContext = getApplicationContext();
        mainThreadId = android.os.Process.myTid();
        ins = this;
        mHandler = new Handler();
    }

    public static MyApplication getIns() {
        return ins;
    }

    // 返回上下文
    public static Context getContext() {
        return mContext;
    }

    // 返回主线程的id
    public static long getMainThreadId() {
        return mainThreadId;
    }

    // 返回主线程的Handler
    public static Handler getHanlder() {
        return mHandler;
    }
}
