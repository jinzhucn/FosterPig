package com.minlu.fosterpig.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.adapter.SureWarnAdapter;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.util.ViewsUitls;

import java.util.ArrayList;

/**
 * Created by user on 2016/11/23.
 */
public class SureWarnFragment extends BaseFragment<String> {
    private ArrayList<String> list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SureWarnAdapter sureWarnAdapter;

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
}
