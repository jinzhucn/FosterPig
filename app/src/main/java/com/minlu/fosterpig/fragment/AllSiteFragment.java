package com.minlu.fosterpig.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.adapter.MyExpandableListViewAdapter;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.manager.ThreadManager;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by user on 2016/11/23.
 */
public class AllSiteFragment extends BaseFragment<ArrayList> implements SwipeRefreshLayout.OnRefreshListener {

    private List<ArrayList> list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExpandableListView expandableListView;
    private int currentExpandGroup = -1;
    private Runnable mRefreshThread;
    private MyExpandableListViewAdapter myExpandableListViewAdapter;
    private String mResultString;

    @Override
    protected void onSubClassOnCreateView() {
        loadDataAndRefresh();
    }

    @Override
    protected View onCreateSuccessView() {

        View inflate = ViewsUitls.inflate(R.layout.layout_all_site);

        swipeRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_all_site);
        //改变加载显示的颜色
        swipeRefreshLayout.setColorSchemeColors(StringsFiled.SWIPE_REFRESH_FIRST_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_SECOND_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_THIRD_ROUND_COLOR);
        //设置背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(StringsFiled.SWIPE_REFRESH_BACKGROUND);
        //设置初始时的大小
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        //设置监听
        swipeRefreshLayout.setOnRefreshListener(this);

        expandableListView = (ExpandableListView) inflate.findViewById(R.id.elv_all_site);
        expandableListView.setGroupIndicator(null);

        myExpandableListViewAdapter = new MyExpandableListViewAdapter(list);
        expandableListView.setAdapter(myExpandableListViewAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            //v : 条目的view对象
            //groupPosition :条目的位置
            //id : 条目的id
            //return : true:表示执行完成
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                //打开关闭条目,打开条目的时候关闭其他条目,同时让当前打开条目置顶
                if (currentExpandGroup == -1) {
                    //打开的自己
                    expandableListView.expandGroup(groupPosition);//打开点击的组条目
                    currentExpandGroup = groupPosition;
                    expandableListView.setSelectedGroup(groupPosition);
                } else {
                    //关闭组,打开其他组
                    //1.打开的是自己,又点击了自己,关闭自己
                    //2.打开的是自己,又点击其他组,关闭自己,打开其他组,通将其他组置顶
                    if (currentExpandGroup == groupPosition) {
                        //关闭自己
                        expandableListView.collapseGroup(groupPosition);
                        currentExpandGroup = -1;
                    } else {
                        //关闭之前打开的组,打开点击的组
                        expandableListView.collapseGroup(currentExpandGroup);
                        //打开点击的组
                        expandableListView.expandGroup(groupPosition);

                        expandableListView.setSelectedGroup(groupPosition);
                        currentExpandGroup = groupPosition;
                    }
                }

                return true;
            }
        });

        return inflate;
    }

    @Override
    protected ContentPage.ResultState onLoad() {

        OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
        RequestBody formBody = new FormBody.Builder().build();

        String address = SharedPreferencesUtil.getString(
                ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");

        Request request = new Request.Builder()
                .url(address + IpFiled.ALL_SITE_DATA)
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
        }




        list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            ArrayList<String> list1 = new ArrayList<>();
            list1.add("测试");
            list1.add("测试");
            list1.add("测试");
            list1.add("测试");
            list1.add("测试");
            list1.add("测试");
            list.add(list1);
        }


        return chat(list);
    }

    private void analysisJsonDate() {


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
                    ArrayList<String> list1 = new ArrayList<>();
                    list1.add("测试");
                    list1.add("测试");
                    list1.add("测试");
                    list1.add("测试");
                    list1.add("测试");
                    list1.add("测试");
                    ArrayList<String> list2 = new ArrayList<>();
                    list2.add("测试");
                    list2.add("测试");
                    list2.add("测试");
                    list2.add("测试");
                    list2.add("测试");
                    list2.add("测试");
                    list.add(list1);
                    list.add(list2);

                    ViewsUitls.runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            myExpandableListViewAdapter.notifyDataSetChanged();
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
        if (swipeRefreshLayout.isRefreshing()) {
            System.out.println("还在刷新");
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
