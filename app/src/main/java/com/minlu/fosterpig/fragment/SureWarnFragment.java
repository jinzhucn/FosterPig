package com.minlu.fosterpig.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.adapter.SureWarnAdapter;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.manager.ThreadManager;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ViewsUitls;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by user on 2016/11/23.
 */
public class SureWarnFragment extends BaseFragment<String> implements SwipeRefreshLayout.OnRefreshListener {
    private ArrayList<String> list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SureWarnAdapter sureWarnAdapter;
    private Runnable mRefreshThread;

    private boolean requestDataIsSuccess;
    private String mResultString;

    @Override
    protected void onSubClassOnCreateView() {
        loadDataAndRefresh();
    }

    @Override
    protected View onCreateSuccessView() {

        View inflate = ViewsUitls.inflate(R.layout.layout_listview);

        swipeRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_list_view_no_swipe_menu);
        listView = (ListView) inflate.findViewById(R.id.have_swipe_refresh_list_view);

        sureWarnAdapter = new SureWarnAdapter(list);
        listView.setAdapter(sureWarnAdapter);


        //改变加载显示的颜色
        swipeRefreshLayout.setColorSchemeColors(StringsFiled.SWIPE_REFRESH_FIRST_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_SECOND_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_THIRD_ROUND_COLOR);
        //设置背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(StringsFiled.SWIPE_REFRESH_BACKGROUND);
        //设置初始时的大小
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        //设置监听
        swipeRefreshLayout.setOnRefreshListener(this);
        //设置向下拉多少出现刷新
//        swipeRefreshLayout.setDistanceToTriggerSync(100);
        //设置刷新出现的位置
//        swipeRefreshLayout.setProgressViewEndTarget(false, 200);


        return inflate;
    }

    @Override
    protected ContentPage.ResultState onLoad() {

        requestData();

        return chat(list);
    }

    private void requestData() {
        OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
        RequestBody formBody = new FormBody.Builder().add("dtuId", "0").add("selectDate", "").add("start", "0").add("limit", "20").build();

        String address = SharedPreferencesUtil.getString(
                ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");

        Request request = new Request.Builder()
                .url(address + IpFiled.ALL_ALREADY_SURE_WARN)
                .post(formBody)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                mResultString = response.body().string();
                Log.i("okHttp_SUCCESS", mResultString);
                analysisJsonDate();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("=========================onFailure=============================");
            Log.i("okHttp_ERROE", "okHttp is request error");
            requestDataIsSuccess = false;
        }
    }

    private void analysisJsonDate() {

        if (StringUtils.interentIsNormal(mResultString)) {
            System.out.println("mResultString: " + mResultString);





        } else {
            requestDataIsSuccess = false;
        }

    }


    @Override
    public void onRefresh() {

        if (mRefreshThread == null) {
            System.out.println("SureWarnFragment-New-Thread");
            mRefreshThread = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 请求网络数据
                    list.clear();
                    for (int i = 0; i < 10; i++) {
                        list.add("确认信息");
                    }


                    ViewsUitls.runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            sureWarnAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            };
        }
        ThreadManager.getInstance().execute(mRefreshThread);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        System.out.println("WarnInformation-onDestroy");
        if (mRefreshThread != null) {
            System.out.println("线程没有结束");
            ThreadManager.getInstance().cancel(mRefreshThread);
            mRefreshThread = null;
        }
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            System.out.println("还在刷新");
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
