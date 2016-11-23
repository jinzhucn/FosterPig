package com.minlu.fosterpig.adapter;


import com.minlu.fosterpig.base.BaseHolder;
import com.minlu.fosterpig.base.MyBaseAdapter;
import com.minlu.fosterpig.holder.WarnHolder;

import java.util.List;

/**
 * Created by user on 2016/11/22.
 */
public class WarnAdapter extends MyBaseAdapter<String> {

    public WarnAdapter(List<String> list) {
        super(list);
    }

    @Override
    public BaseHolder getHolder() {
        return new WarnHolder();
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
