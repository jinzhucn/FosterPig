package com.minlu.fosterpig.fragment;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.adapter.AllWarnAdapter;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.manager.ThreadManager;
import com.minlu.fosterpig.util.ViewsUitls;

import java.util.ArrayList;

/**
 * Created by user on 2016/11/24.
 */
public class AllWarnFragment extends BaseFragment<String> implements SwipeRefreshLayout.OnRefreshListener {
    private ArrayList<String> objects;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AllWarnAdapter mAllWarnAdapter;
    private Runnable mRefreshThread;

    @Override
    protected void onSubClassOnCreateView() {
        loadDataAndRefresh();
    }

    @Override
    protected View onCreateSuccessView() {
        View inflate = ViewsUitls.inflate(R.layout.layout_swipe_menu_listview);

        swipeRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_list_view_have_swipe_menu);
        //改变加载显示的颜色
        swipeRefreshLayout.setColorSchemeColors(StringsFiled.SWIPE_REFRESH_FIRST_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_SECOND_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_THIRD_ROUND_COLOR);
        //设置背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(StringsFiled.SWIPE_REFRESH_BACKGROUND);
        //设置初始时的大小
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        //设置监听
        swipeRefreshLayout.setOnRefreshListener(this);

        SwipeMenuListView mListView = (SwipeMenuListView) inflate.findViewById(R.id.swipe_menu_list_view);
        mAllWarnAdapter = new AllWarnAdapter(objects);
        mListView.setAdapter(mAllWarnAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(ViewsUitls.getContext());
                // set item background
                openItem.setBackground(R.drawable.selector_swipe_menu_bottom_item_background);
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("确认");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

            }
        };

        // set creator
        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        objects.remove(position);
                        mAllWarnAdapter.notifyDataSetChanged();

                        Toast.makeText(ViewsUitls.getContext(), "Open", Toast.LENGTH_SHORT).show();
                        break;
                }
                // ★★★★★false : close the menu; true : not close the menu
                return false;
            }
        });


        return inflate;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    protected ContentPage.ResultState onLoad() {

        objects = new ArrayList<>();
        objects.add("测试");
        return chat(objects);
    }

    @Override
    public void onRefresh() {

        if (mRefreshThread == null) {
            System.out.println("WarnInformation-New-Thread");
            mRefreshThread = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 请求网络数据
                    objects.clear();
                    objects.add("刷新的数据");
                    objects.add("刷新的数据");
                    objects.add("刷新的数据");
                    objects.add("刷新的数据");
                    objects.add("刷新的数据");


                    ViewsUitls.runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            mAllWarnAdapter.notifyDataSetChanged();
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
