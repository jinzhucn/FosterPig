package com.minlu.fosterpig.adapter;


import com.minlu.fosterpig.base.BaseHolder;
import com.minlu.fosterpig.base.MyBaseAdapter;
import com.minlu.fosterpig.holder.SureWarnHolder;
import com.minlu.fosterpig.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/11/22.
 */
public class SureWarnAdapter extends MyBaseAdapter<String> {

    private List<String> middleList=new ArrayList<>();
    private String mSaveList="KHGIKUGHOIHOIHOILHLIHLK";

    public SureWarnAdapter(List<String> list) {
        super(list);
    }

    @Override
    public BaseHolder getHolder() {
        return new SureWarnHolder();
    }

    @Override
    public List<String> onLoadMore() {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int middle = getDataSize();
        if (middle % 10 != 0) {
            return new ArrayList<>();
        } else {
            int c = middle / 10 + 1;
            requestDate(c);
            if (StringUtils.interentIsNormal(mSaveList)) {
                parseJson();
                return middleList;
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean hasMore() {
        return true;
    }

    private void requestDate(int page) {



    }

    private void parseJson() {
        middleList.clear();

        for (int i=0;i<10;i++){
            middleList.add("ceshi ");
        }

    }
}
