package com.minlu.fosterpig.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.bean.RootCtrlCenter;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.HttpConstants;
import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.activity.LiveActivity;
import com.minlu.fosterpig.activity.VideoTwoListActivity;
import com.minlu.fosterpig.adapter.VideoAreaListAdapter;
import com.minlu.fosterpig.haikang.LoginCameraData;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ToastUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by user on 2017/1/17.
 */

public class VideoListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View mLoading;
    private View mEmpty;
    private View mError;
    private ListView mListView;

    private static final int LOADING_LAYOUT = 1;
    private static final int EMPTY_LAYOUT = 2;
    private static final int ERROR_LAYOUT = 3;
    private static final int LIST_VIEW_LAYOUT = 4;
    private List<SubResourceNodeBean> areaData = new ArrayList<>();
    private VideoAreaListAdapter videoAreaListAdapter;

    private MyHandler myHandler;
    private boolean isOpenTwoList = false;
    private int parentNodeType = -1;
    private int parentId = -1;

    static class MyHandler extends Handler {
        WeakReference<VideoListFragment> mVideoListFragment;

        MyHandler(VideoListFragment videoListFragment) {
            mVideoListFragment = new WeakReference<>(videoListFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoListFragment videoListFragment = mVideoListFragment.get();
            switch (msg.what) {
                case LOADING_LAYOUT:
                    videoListFragment.showWhichLayout(LOADING_LAYOUT);
                    break;
                case EMPTY_LAYOUT:
                    videoListFragment.showWhichLayout(EMPTY_LAYOUT);
                    break;
                case ERROR_LAYOUT:
                    videoListFragment.myHandler.sendEmptyMessage(ERROR_LAYOUT);
                    break;
                case LIST_VIEW_LAYOUT:
                    videoListFragment.showWhichLayout(LIST_VIEW_LAYOUT);
                    videoListFragment.videoAreaListAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        isOpenTwoList = bundle.getBoolean(StringsFiled.GET_TWO_LIST_VIEW, false);
        if (isOpenTwoList) {
            parentNodeType = bundle.getInt(StringsFiled.PARENT_NODE_TYPE, -1);
            parentId = bundle.getInt(StringsFiled.PARENT_ID, -1);
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View inflate = ViewsUitls.inflate(R.layout.activity_video_list);

        mLoading = inflate.findViewById(R.id.video_list_loading);
        mEmpty = inflate.findViewById(R.id.video_list_empty);
        mError = inflate.findViewById(R.id.video_list_error);

        mListView = (ListView) inflate.findViewById(R.id.video_list_lv);
        videoAreaListAdapter = new VideoAreaListAdapter(areaData);
        mListView.setAdapter(videoAreaListAdapter);
        mListView.setOnItemClickListener(this);

        myHandler = new MyHandler(this);

        showWhichLayout(LOADING_LAYOUT);

        if (isOpenTwoList) {
            getSubResourceList(parentNodeType, parentId);
        } else {
            startLogin();
        }
        return inflate;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SubResourceNodeBean subResourceNodeBean = areaData.get(position);
        if (HttpConstants.NodeType.TYPE_CAMERA_OR_DOOR == subResourceNodeBean.getNodeType()) {// 被点击的条目是摄像所在站点名称
            Camera camera = VMSNetSDK.getInstance().initCameraInfo(subResourceNodeBean);
            if (VMSNetSDK.getInstance().isHasLivePermission(camera)) {// 判断camera是否是有权限
                Intent intent = new Intent(getContext(), LiveActivity.class);
                intent.putExtra(StringsFiled.CAMERA_INFORMATION, camera);
                startActivity(intent);
            } else {
                ToastUtil.showToast(ViewsUitls.getContext(), "摄像头无权限打开");
            }
        } else {// 被点击的条目是站点所在区域
            System.out.println();
            Intent intent = new Intent(getContext(), VideoTwoListActivity.class);
            intent.putExtra(StringsFiled.ACTIVITY_TITLE, "视频—站点");
            intent.putExtra(StringsFiled.PARENT_NODE_TYPE, subResourceNodeBean.getNodeType());
            intent.putExtra(StringsFiled.PARENT_ID, subResourceNodeBean.getId());
            getContext().startActivity(intent);
        }
    }

    private void startLogin() {
        VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                myHandler.sendEmptyMessage(ERROR_LAYOUT);
            }

            @Override
            public void loading() {
                System.out.println("登录正在加载中");
            }

            @Override
            public void onSuccess(Object data) {
                if (data instanceof LoginData) {
                    // 成功登陆后保存LoginData对象信息和url

                    LoginCameraData.getInstance().setLoginData((LoginData) data);
                    LoginCameraData.getInstance().setLoginIpAddress(IpFiled.VIDEO_LOGIN_IP);
                    getRootControlCenter();
                } else {
                    myHandler.sendEmptyMessage(ERROR_LAYOUT);
                }
            }

        });
        // 登录请求
        VMSNetSDK.getInstance().login(IpFiled.VIDEO_LOGIN_IP, IpFiled.VIDEO_LOGIN_USER_NAME, IpFiled.VIDEO_LOGIN_PASS_WORD, ViewsUitls.getMacAddress(getContext()));
    }

    /**
     * 获取控制中心信息
     */
    public void getRootControlCenter() {
        VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof RootCtrlCenter) {

                    getSubResourceList(Integer.parseInt(((RootCtrlCenter) obj).getNodeType()), ((RootCtrlCenter) obj).getId());
                } else {
                    myHandler.sendEmptyMessage(ERROR_LAYOUT);
                }
            }

            @Override
            public void onFailure() {
                super.onFailure();
                myHandler.sendEmptyMessage(ERROR_LAYOUT);
            }
        });
        // 参数一:当前页   参数二:系统类型  1-视频 2-门禁 3-连锁    参数三:每页数量
        VMSNetSDK.getInstance().getRootCtrlCenterInfo(1, HttpConstants.SysType.TYPE_VIDEO, 15);
    }

    /**
     * 获取控制中心下的区域信息或者区域下的站点信息
     */
    private void getSubResourceList(int parentNodeType, int pId) {
        VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof List<?>) {
                    areaData.clear();
                    areaData.addAll((Collection<? extends SubResourceNodeBean>) obj);
                    if (areaData.size() == 0) {
                        myHandler.sendEmptyMessage(EMPTY_LAYOUT);
                    } else {
                        myHandler.sendEmptyMessage(LIST_VIEW_LAYOUT);
                    }
                } else {
                    myHandler.sendEmptyMessage(ERROR_LAYOUT);
                }
            }

            @Override
            public void onFailure() {
                super.onFailure();
                myHandler.sendEmptyMessage(ERROR_LAYOUT);
            }
        });
        // 参数一:当前页  参数二:每页数量  参数三:系统类型  1-视频 2-门禁 3-连锁  参数四:上级类型  参数五:上级id
        VMSNetSDK.getInstance().getSubResourceList(1, 15, HttpConstants.SysType.TYPE_VIDEO, parentNodeType, pId + "");
    }

    private void showWhichLayout(int which) {
        switch (which) {
            case LOADING_LAYOUT:
                mLoading.setVisibility(View.VISIBLE);
                mEmpty.setVisibility(View.GONE);
                mError.setVisibility(View.GONE);
                mListView.setVisibility(View.INVISIBLE);
                break;
            case EMPTY_LAYOUT:
                mLoading.setVisibility(View.GONE);
                mEmpty.setVisibility(View.VISIBLE);
                mError.setVisibility(View.GONE);
                mListView.setVisibility(View.INVISIBLE);
                break;
            case ERROR_LAYOUT:
                mLoading.setVisibility(View.GONE);
                mEmpty.setVisibility(View.GONE);
                mError.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.INVISIBLE);
                break;
            case LIST_VIEW_LAYOUT:
                mLoading.setVisibility(View.GONE);
                mEmpty.setVisibility(View.GONE);
                mError.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onDestroy() {

        // 登出
        LoginData loginData = LoginCameraData.getInstance().getLoginData();
        String url = LoginCameraData.getInstance().getLoginIpAddress();
        if (loginData != null && !StringUtils.isEmpty(url)) {
            if (VMSNetSDK.getInstance().logout()) {
                LoginCameraData.getInstance().setLoginData(null);
                LoginCameraData.getInstance().setLoginIpAddress(null);
            }
        }

        super.onDestroy();
    }
}
