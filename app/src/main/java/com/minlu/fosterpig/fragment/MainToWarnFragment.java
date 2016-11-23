package com.minlu.fosterpig.fragment;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.adapter.WarnAdapter;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.util.ViewsUitls;

import java.util.ArrayList;

/**
 * Created by user on 2016/11/22.
 */
public class MainToWarnFragment extends BaseFragment<String> {

    private ArrayList<String> objects;
    private WarnAdapter mWarnAdapter;

    @Override
    protected void onSubClassOnCreateView() {
        loadDataAndRefresh();
    }

    @Override
    protected View onCreateSuccessView() {

        View inflate = ViewsUitls.inflate(R.layout.layout_swipe_menu_listview);
        SwipeMenuListView mListView = (SwipeMenuListView) inflate.findViewById(R.id.swipe_menu_list_view);
        mWarnAdapter = new WarnAdapter(objects);
        mListView.setAdapter(mWarnAdapter);

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
                        mWarnAdapter.notifyDataSetChanged();

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

        switch (getBundleValue()) {

            case StringsFiled.MAIN_TO_WARN_VALUE_AMMONIA:
                objects.add("测试");
                objects.add("测试");
                break;

            case StringsFiled.MAIN_TO_WARN_VALUE_TEMPERATURE:
                objects.add("测试");
                objects.add("测试");
                objects.add("测试");
                objects.add("测试");
                break;

            case StringsFiled.MAIN_TO_WARN_VALUE_HUMIDITY:

                objects.add("测试");
                objects.add("测试");
                objects.add("测试");
                objects.add("测试");
                objects.add("测试");
                break;

            case StringsFiled.SELECT_WARN_INFORMATION_TAB:

                objects.add("测试");
                break;

        }


        return chat(objects);
    }

}
