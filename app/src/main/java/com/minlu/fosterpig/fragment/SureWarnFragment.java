package com.minlu.fosterpig.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.adapter.SureWarnAdapter;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.manager.ThreadManager;
import com.minlu.fosterpig.util.ViewsUitls;

import java.util.ArrayList;

/**
 * Created by user on 2016/11/23.
 */
public class SureWarnFragment extends BaseFragment<String> implements SwipeRefreshLayout.OnRefreshListener {
    private ArrayList<String> list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SureWarnAdapter sureWarnAdapter;
    private Runnable mRefreshThread;

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
        list = new ArrayList<>();

        list.add("确认信息");
        list.add("确认信息");
        list.add("确认信息");
        list.add("确认信息");
        list.add("确认信息");
        list.add("确认信息");
        list.add("确认信息");
        list.add("确认信息");
        list.add("确认信息");
        list.add("确认信息");
        list.add("确认信息");
        list.add("确认信息");
        list.add("确认信息");
        list.add("确认信息");
        list.add("确认信息");
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
                    list.add("刷新的数据");
                    list.add("刷新的数据");
                    list.add("刷新的数据");
                    list.add("刷新的数据");
                    list.add("刷新的数据");


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
        if (swipeRefreshLayout.isRefreshing()) {
            System.out.println("还在刷新");
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
