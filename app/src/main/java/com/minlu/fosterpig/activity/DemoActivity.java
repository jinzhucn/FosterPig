/**
 * <p>DemoActivity Class</p>
 *
 * @author zhuzhenlei 2014-7-17
 * @version V1.0
 * @modificationHistory
 * @modify by user:
 * @modify by reason:
 */
package com.minlu.fosterpig.activity;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.haikang.PlaySurfaceView;

import org.MediaPlayer.PlayM4.Player;

/**
 * <pre>
 *  ClassName  DemoActivity Class
 * </pre>
 *
 * @author zhuzhenlei
 * @version V1.0
 */
public class DemoActivity extends Activity implements Callback {

    private Button mLoginButton = null;
    private Button mPreviewButton = null;

    private SurfaceView mSurfaceView = null;

    private EditText mIpAddress = null;
    private EditText mPort = null;
    private EditText mUserName = null;
    private EditText mPssWord = null;

    private int mLoginId = -1;                // return by NET_DVR_Login_v30
    private int m_iPlayID = -1;                // return by NET_DVR_RealPlay_V30
    private int m_iPlaybackID = -1;                // return by NET_DVR_PlayBackByTime

    private int m_iPort = -1;                // play port
    private int m_iStartChan = 0;                // start channel no
    private int m_iChanNum = 0;                //channel number
    private static PlaySurfaceView[] playView = new PlaySurfaceView[4];

    private final String TAG = "DemoActivity";

    private boolean m_bMultiPlay = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        if (!initeSdk()) {
            this.finish();
            return;
        }

        if (!initeActivity()) {
            this.finish();
            return;
        }

        // TODO 设置四大登录参数
        mIpAddress.setText(R.string.text_ip);
        mPort.setText(R.string.text_port);
        mUserName.setText(R.string.text_user);
        mPssWord.setText(R.string.text_password);
    }

    // TODO =============================================SurfaceView接口=====================================================
    //@Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Log.i(TAG, "surface is created" + m_iPort);
        if (-1 == m_iPort) {
            return;
        }
        Surface surface = holder.getSurface();
        if (surface.isValid()) {
            if (!Player.getInstance().setVideoWindow(m_iPort, 0, holder)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    //@Override  
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    //@Override  
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "Player setVideoWindow release!" + m_iPort);
        if (-1 == m_iPort) {
            return;
        }
        if (holder.getSurface().isValid()) {
            if (!Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }
    //  =============================================SurfaceView接口=====================================================


    // TODO =============================================onSaveInstanceState缓存=====================================================
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("m_iPort", m_iPort);
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        m_iPort = savedInstanceState.getInt("m_iPort");
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
    }
//  =============================================onSaveInstanceState缓存=====================================================

    // TODO =============================================初始化=====================================================

    /**
     * @return true - success;false - fail
     */
    private boolean initeSdk() {
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        // TODO 启用日志 后期可删除
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, Environment.getExternalStorageDirectory().getPath() + "/", true);
        return true;
    }

    // GUI init
    private boolean initeActivity() {
        findViews();
        mSurfaceView.getHolder().addCallback(this);
        setListeners();
        return true;
    }

    // get controller instance
    private void findViews() {
        mLoginButton = (Button) findViewById(R.id.btn_Login);
        mPreviewButton = (Button) findViewById(R.id.btn_Preview);

        mSurfaceView = (SurfaceView) findViewById(R.id.Sur_Player);

        mIpAddress = (EditText) findViewById(R.id.EDT_IPAddr);
        mPort = (EditText) findViewById(R.id.EDT_Port);
        mUserName = (EditText) findViewById(R.id.EDT_User);
        mPssWord = (EditText) findViewById(R.id.EDT_Psd);
    }

    // listen
    private void setListeners() {
        mLoginButton.setOnClickListener(Login_Listener);
        mPreviewButton.setOnClickListener(Preview_Listener);
    }
    //  =============================================初始化=====================================================

    // TODO =============================================登录具体实现代码=====================================================
    private OnClickListener Login_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {
                if (mLoginId < 0) {//  此处没有登录成功过，所以开始登录代码
                    // login on the device
                    mLoginId = loginDevice();// 调用登录方法获取登录后返回的id
                    if (mLoginId < 0) {
                        Log.e(TAG, "登录返回的id小于0，登录失败!");
                        return;
                    }
                    // get instance of exception callback and set
                    ExceptionCallBack exceptionCallBack = getExceptionCallBack();
                    if (exceptionCallBack == null) {
                        Log.e(TAG, "创建ExceptionCallBack回调接口对象失败");
                        return;
                    }

                    if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(exceptionCallBack)) {
                        Log.e(TAG, "将创建好的ExceptionCallBack回调接口设置进某个地方");
                        return;
                    }

                    mLoginButton.setText(R.string.text_btn_login_out);
                    Log.i(TAG, "登录成功");
                } else {//  此处登录成功过，所以开始登出代码
                    // whether we have logout
                    if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(mLoginId)) {
                        Log.e(TAG, "登出操作失败");
                        return;
                    }
                    mLoginButton.setText(R.string.text_btn_login);
                    mLoginId = -1;
                }
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };

    /**
     * @return login ID
     */
    private int loginDevice() {
        // 创建对象 该对象用来存储登录成功后的信息
        NET_DVR_DEVICEINFO_V30 mNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == mNetDvrDeviceInfoV30) {
            Log.e(TAG, "HKNetDvrDeviceInfoV30对象创建失败");
            return -1;
        }

        // 登录四大要素
        String strIP = mIpAddress.getText().toString();
        int nPort = Integer.parseInt(mPort.getText().toString());
        String strUser = mUserName.getText().toString();
        String strPsd = mPssWord.getText().toString();

        // 使用四大要素与对象作为参数调用登录方法
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIP, nPort, strUser, strPsd, mNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            Log.e(TAG, "NET_DVR_Login_V30登录方法失败!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        System.out.println();// TODO debug点
        if (mNetDvrDeviceInfoV30.byChanNum > 0) {// 设备模拟通道个数
            m_iStartChan = mNetDvrDeviceInfoV30.byStartChan;// 模拟通道起始通道号
            m_iChanNum = mNetDvrDeviceInfoV30.byChanNum; // 设备模拟通道个数
        } else if (mNetDvrDeviceInfoV30.byIPChanNum > 0) {// 设备最大数字通道个数，低 8 位
            m_iStartChan = mNetDvrDeviceInfoV30.byStartDChan;// 起始数字通道号
            m_iChanNum = mNetDvrDeviceInfoV30.byIPChanNum + mNetDvrDeviceInfoV30.byHighDChanNum * 256;// 其中byHighDChanNum为数字通道个数，高 8 位
        }
        Log.i(TAG, "NET_DVR_Login_V30登录方法Successful!");

        return iLogID;
    }
//  =============================================登录具体实现代码=====================================================

    private OnClickListener Preview_Listener = new OnClickListener() {
        public void onClick(View v) {
            if (mLoginId < 0) {
                Log.e(TAG, "请先登录");
                return;
            }
            if (m_iChanNum > 1) {// 多屏幕播放
                if (!m_bMultiPlay) {
                    startMultiPreview();
                    m_bMultiPlay = true;
                    mPreviewButton.setText(R.string.text_btn_stop);
                } else {
                    stopMultiPreview();
                    m_bMultiPlay = false;
                    mPreviewButton.setText(R.string.text_btn_preview);
                }
            } else {// 单屏幕播发
                if (m_iPlayID < 0) {
                    startSinglePreview();
                } else {
                    stopSinglePreview();
                    mPreviewButton.setText(R.string.text_btn_preview);
                }
            }
        }
    };

    // TODO =============================================单屏幕播放停止=====================================================
    private void startSinglePreview() {
        if (m_iPlaybackID >= 0) {
            Log.i(TAG, "Please stop palyback first");
            return;
        }

        // 获取包含有自定义播放方法的回调接口对象
        RealPlayCallBack fRealDataCallBack = getRealPlayerCallBack();
        if (fRealDataCallBack == null) {
            Log.e(TAG, "RealPlayCallBack回调接口对象创建failed!");
            return;
        }
        Log.i(TAG, "m_iStartChan:" + m_iStartChan);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan;// 通道号
        previewInfo.dwStreamType = 1; // 码流类型：0-主码流，1-子码流，2-码流 3，3-虚拟码流，以此类推
        previewInfo.bBlocked = 1;// 0- 非阻塞取流，1- 阻塞取流
        // HCNetSDK start preview
        m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(mLoginId, previewInfo, fRealDataCallBack);
        if (m_iPlayID < 0) {
            Log.e(TAG, "NET_DVR_RealPlay_V40播放监控方法失败!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        Log.i(TAG, "NET_DVR_RealPlay_V40播放监控方法sucess");
        mPreviewButton.setText(R.string.text_btn_stop);
    }

    private void stopSinglePreview() {
        if (m_iPlayID < 0) {
            Log.e(TAG, "播放返回的id小于0，没有播放成功过");
            return;
        }

        //  net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID)) {
            Log.e(TAG, "NET_DVR_StopRealPlay停止播放方法失败!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        m_iPlayID = -1;
        stopSinglePlayer();
    }

    private void stopSinglePlayer() {
        Player.getInstance().stopSound();
        // player stop play
        if (!Player.getInstance().stop(m_iPort)) {
            Log.e(TAG, "stop is failed!");
            return;
        }

        if (!Player.getInstance().closeStream(m_iPort)) {
            Log.e(TAG, "closeStream is failed!");
            return;
        }
        if (!Player.getInstance().freePort(m_iPort)) {
            Log.e(TAG, "freePort is failed!" + m_iPort);
            return;
        }
        m_iPort = -1;
    }
//  =============================================单屏幕播放停止=====================================================

    private void startMultiPreview() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int i;
        for (i = 0; i < 4; i++) {
            if (playView[i] == null) {
                playView[i] = new PlaySurfaceView(this);
                playView[i].setParam(metric.widthPixels);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = playView[i].getCurHeight() - (i / 2) * playView[i].getCurHeight();
                params.leftMargin = (i % 2) * playView[i].getCurWidth();
                params.gravity = Gravity.BOTTOM | Gravity.START;// Gravity.START原先为Gravity.LEFT
                addContentView(playView[i], params);
            }
            playView[i].startPreview(mLoginId, m_iStartChan + i);
        }
        m_iPlayID = playView[0].m_iPreviewHandle;
    }

    private void stopMultiPreview() {
        int i;
        for (i = 0; i < 4; i++) {
            playView[i].stopPreview();
        }
        m_iPlayID = -1;
    }

    /**
     */

    /**
     * @return exception instance
     */
    private ExceptionCallBack getExceptionCallBack() {
        return new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("***************************************此处是ExceptionCallBack回调接口的回调方法****************************************");
                System.out.println("recv exception, type:" + iType);
            }
        };
    }

    /**
     * @return callback instance
     */
    private RealPlayCallBack getRealPlayerCallBack() {
        return new RealPlayCallBack() {
            public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
                // player channel 1
                DemoActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
            }
        };
    }

    /**
     * @param iPlayViewNo - player channel [in]
     * @param iDataType   - data type [in]
     * @param pDataBuffer - data buffer [in]
     * @param iDataSize   - data size [in]
     * @param iStreamMode - stream mode [in]
     */
    public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
            if (m_iPort >= 0) {
                return;
            }
            m_iPort = Player.getInstance().getPort();
            if (m_iPort == -1) {
                Log.e(TAG, "getPort is failed with: " + Player.getInstance().getLastError(m_iPort));
                return;
            }
            Log.i(TAG, "getPort succ with: " + m_iPort);
            if (iDataSize > 0) {
                if (!Player.getInstance().setStreamOpenMode(m_iPort, iStreamMode))  //set stream mode
                {
                    Log.e(TAG, "setStreamOpenMode failed");
                    return;
                }
                if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) //open stream
                {
                    Log.e(TAG, "openStream failed");
                    return;
                }
                if (!Player.getInstance().play(m_iPort, mSurfaceView.getHolder())) {
                    Log.e(TAG, "play failed");
                    return;
                }
                if (!Player.getInstance().playSound(m_iPort)) {
                    Log.e(TAG, "playSound failed with error code:" + Player.getInstance().getLastError(m_iPort));
//                        return;  // 最后一个return不需要，已经走完代码准备出去了
                }
            }
        } else {
            if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
//		    		Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
                for (int i = 0; i < 4000 && m_iPlaybackID >= 0; i++) {
                    if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize))
                        Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
                    else
                        break;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }
                }
            }

        }

    }

    /**
     */
    public void Cleanup() {
        // release player resource

        Player.getInstance().freePort(m_iPort);
        m_iPort = -1;

        // release net SDK resource
        HCNetSDK.getInstance().NET_DVR_Cleanup();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                stopSinglePlayer();
                Cleanup();
                android.os.Process.killProcess(android.os.Process.myPid());// 杀掉主线程，代表应用全部结束
                break;
            default:
                break;
        }

        return true;
    }
}
