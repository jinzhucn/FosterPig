package com.minlu.fosterpig.adapter;


import com.minlu.fosterpig.base.BaseHolder;
import com.minlu.fosterpig.base.MyBaseAdapter;
import com.minlu.fosterpig.holder.SureWarnHolder;

import java.util.List;

/**
 * Created by user on 2016/11/22.
 */
public class SureWarnAdapter extends MyBaseAdapter<String> {

    public SureWarnAdapter(List<String> list) {
        super(list);
    }

    @Override
    public BaseHolder getHolder() {
        return new SureWarnHolder();
    }

    @Override
    public List<String> onLoadMore() {
        return null;
    }

    @Override
    public boolean hasMore() {
        return false;
    }
}
