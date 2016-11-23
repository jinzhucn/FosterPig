package com.minlu.fosterpig.util;

import android.content.Context;
import android.view.View;

import com.minlu.fosterpig.base.MyApplication;

public class ViewsUitls {

    public static Context getContext() {
        return MyApplication.getContext();
    }


    /**
     * dip转px
     */
    public static int dptopx(int dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        // px = dip * density
        // 3.3 3.8 3
        // 3.6 4.1 4
        return (int) (dip * density + 0.5);
    }

    /**
     * xml 转成View对象
     *
     * @param id
     * @return
     */
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    /**
     * 在主线程中执行任务 模仿runOut。。。。
     *
     * @param task
     */
    public static void runInMainThread(Runnable task) {
        if (MyApplication.getMainThreadId() == android.os.Process.myTid()) {
            // 当前就是主线程，直接执行task
            task.run();
        } else {
            // 在子线程，post给主线程
            MyApplication.getHanlder().post(task);
        }
    }

}
