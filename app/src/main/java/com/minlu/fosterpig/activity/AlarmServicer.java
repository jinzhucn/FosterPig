package com.minlu.fosterpig.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.customview.MyMediaPlayer;
import com.minlu.fosterpig.observer.MySubject;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ViewsUitls;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmServicer extends Service {

    private NotificationManager mNotificationManager;
    private MyMediaPlayer myMediaPlayer;
    private Timer timer;
    private TimerTask timerTask;
    private boolean isAlarm;
    private Builder mBuilder;
    private String postBack;
    private int isStart = 0;
    private String msg;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 通知栏准备
        initNotification();
        // 多媒体播放准备
        prepareMediaPlayer();

        // 一开服务就允许声音播放
        SharedPreferencesUtil.saveboolean(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY, true);
        // 一开服务的系统时间
        SharedPreferencesUtil.saveLong(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY_TIME, System.currentTimeMillis());

        // 0秒后开启定时器，定时循环间隔60秒
        timer = new Timer();
        timerTask = new TimerTask() {

            @Override
            public void run() {
                // TODO 请求一次网络看是否要报警
                try {
                    postBack = "在这里请求报警后的结果false";

                    if (StringUtils.interentIsNormal(postBack)) {
                        if (postBack.contains("false")) {
                            isAlarm = false;
                            msg = "暂无报警信息";
                        } else if (postBack.contains("true")) {
                            msg = postBack;// TODO 是否需要具体的报警信息
                            isAlarm = true;
                        }
                    } else {
                        // 网络请求失败
                        isAlarm = false;
                        msg = "报警信息,请求失败!";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TODO 测试用
//				msg = "dasdasdasd";
//				isAlarm = true;
                // TODO 测试用

                if (!SharedPreferencesUtil.getboolean(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY, false)) {
                    long timeInterval = SharedPreferencesUtil.getLong(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY_TIME, -1) - System.currentTimeMillis();
                    if (timeInterval < 0) {// 如果时间间隔小于0，说明暂停报警声音时间超过，可重新报警
                        SharedPreferencesUtil.saveboolean(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY, true);
                    }
                }

                if (SharedPreferencesUtil.getboolean(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY, false)) {
                    if (isAlarm) {
                        isStart++;
                        myMediaPlayer.start();
                    } else {
                        if (isStart != 0) {
                            myMediaPlayer.pause();
                            isStart = 0;
                        }
                    }
                }
                mBuilder.setContentText(msg);
                startNotification();
            }
        };
        timer.schedule(timerTask, 0, 10000);

        return super.onStartCommand(intent, flags, startId);
    }

    private void prepareMediaPlayer() {
        AssetManager am = getAssets();// 获得该应用的AssetManager
        try {
            AssetFileDescriptor afd = am.openFd("alarm.mp3");
            myMediaPlayer = new MyMediaPlayer();
            MySubject.getInstance().add(myMediaPlayer);
            myMediaPlayer.setDataSource(afd.getFileDescriptor());
            myMediaPlayer.prepare(); // 准备
            myMediaPlayer.setLooping(true);
            myMediaPlayer.isPlaying();
            // mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            // @Override
            // public void onCompletion(MediaPlayer mp) {
            // mediaPlayer.start();
            // // mediaPlayer.setLooping(true);
            // }
            // });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new Builder(this);
        // //PendingIntent 跳转动作
        Intent intent2 = new Intent(ViewsUitls.getContext(),
                NotificationToWarnActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent2, 0);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker("系统警报")
                .setContentTitle("系统警报")
                .setContentText("当报警声响起，请点击通知栏进入设置页面关闭")
                .setContentIntent(pendingIntent);
    }

    private void startNotification() {
        Notification mNotification = mBuilder.build();
        // 设置通知 消息 图标
        mNotification.icon = R.mipmap.ic_launcher;
        // 在通知栏上点击此通知后自动清除此通知
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;// FLAG_ONGOING_EVENT
        // 在顶部常驻，可以调用下面的清除方法去除
        // FLAG_AUTO_CANCEL
        // 点击和清理可以去调
        // 设置显示通知时的默认的发声、震动、Light效果
        // mNotification.defaults = Notification.DEFAULT_VIBRATE;
        // 设置发出消息的内容
        mNotification.tickerText = "系统报警";
        // 设置发出通知的时间
        mNotification.when = System.currentTimeMillis();
        // mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        // //在通知栏上点击此通知后自动清除此通知
        // mNotification.setLatestEventInfo(this, "常驻测试",
        // "使用cancel()方法才可以把我去掉哦", null); //设置详细的信息 ,这个方法现在已经不用了
        mNotificationManager.notify(100, mNotification);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {

        myMediaPlayer.stop();
        myMediaPlayer.release();
        MySubject.getInstance().del(myMediaPlayer);
        if (timer != null)
            timer.cancel();
        timer = null;
        timerTask = null;

        if (mNotificationManager != null) {
            mNotificationManager.cancel(100);
        }
        super.onDestroy();
    }
}
