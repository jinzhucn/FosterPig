package com.minlu.fosterpig.fragment;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.util.ViewsUitls;

import java.util.ArrayList;

/**
 * Created by user on 2016/11/23.
 */
public class SureWarnFragment extends BaseFragment<String> {
    private ArrayList<String> list;

    @Override
    protected void onSubClassOnCreateView() {
        loadDataAndRefresh();
    }

    @Override
    protected View onCreateSuccessView() {
        TextView textView = new TextView(ViewsUitls.getContext());
        textView.setText(list.get(0));
        textView.setTextColor(ContextCompat.getColor(ViewsUitls.getContext(), R.color.black));
        return textView;
    }

    @Override
    protected ContentPage.ResultState onLoad() {
        list = new ArrayList<>();

        list.add("确认信息");

        return chat(list);
    }
}
