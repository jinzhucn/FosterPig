package com.minlu.fosterpig.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.bean.RootCtrlCenter;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.HttpConstants;
import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.haikang.LoginCameraData;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ViewsUitls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by user on 2017/1/17.
 */

public class VideoListFragment extends Fragment {

    private View mLoading;
    private View mEmpty;
    private View mError;
    private ListView mListView;

    private final int LOADING_LAYOUT = 1;
    private final int EMPTY_LAYOUT = 2;
    private final int ERROR_LAYOUT = 3;
    private final int LIST_VIEW_LAYOUT = 4;
    private List<SubResourceNodeBean> areaData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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

        VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                showWhichLayout(ERROR_LAYOUT);
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
                }
            }

        });
        // 登录请求
        VMSNetSDK.getInstance().login(IpFiled.VIDEO_LOGIN_IP, IpFiled.VIDEO_LOGIN_USER_NAME, IpFiled.VIDEO_LOGIN_PASS_WORD, ViewsUitls.getMacAddress(getContext()));

        showWhichLayout(LOADING_LAYOUT);

        return inflate;
    }

    private void showWhichLayout(int which) {
        switch (which) {
            case LOADING_LAYOUT:
                mLoading.setVisibility(View.VISIBLE);
                mEmpty.setVisibility(View.GONE);
                mError.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
                break;
            case EMPTY_LAYOUT:
                mLoading.setVisibility(View.GONE);
                mEmpty.setVisibility(View.VISIBLE);
                mError.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
                break;
            case ERROR_LAYOUT:
                mLoading.setVisibility(View.GONE);
                mEmpty.setVisibility(View.GONE);
                mError.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                break;
            case LIST_VIEW_LAYOUT:
                mLoading.setVisibility(View.GONE);
                mEmpty.setVisibility(View.GONE);
                mError.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                break;
        }
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
                }
            }

            @Override
            public void onFailure() {
                super.onFailure();
                showWhichLayout(ERROR_LAYOUT);
            }
        });
        // 参数一:当前页   参数二:系统类型  1-视频 2-门禁 3-连锁    参数三:每页数量
        VMSNetSDK.getInstance().getRootCtrlCenterInfo(1, HttpConstants.SysType.TYPE_VIDEO, 15);
    }

    /**
     * 获取控制中心下的区域信息
     */
    private void getSubResourceList(int parentNodeType, int pId) {
        VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof List<?>) {
                    areaData = new ArrayList<>();
                    areaData.addAll((Collection<? extends SubResourceNodeBean>) obj);
                    if (areaData.size() == 0) {
                        showWhichLayout(EMPTY_LAYOUT);
                    } else {
                        showWhichLayout(LIST_VIEW_LAYOUT);


                    }
                }
            }

            @Override
            public void onFailure() {
                super.onFailure();
                showWhichLayout(ERROR_LAYOUT);
            }
        });
        // 参数一:当前页  参数二:每页数量  参数三:系统类型  1-视频 2-门禁 3-连锁  参数四:上级类型  参数五:上级id
        VMSNetSDK.getInstance().getSubResourceList(1, 15, HttpConstants.SysType.TYPE_VIDEO, parentNodeType, pId + "");
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
