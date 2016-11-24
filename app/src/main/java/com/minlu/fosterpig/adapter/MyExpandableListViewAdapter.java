package com.minlu.fosterpig.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.util.ViewsUitls;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/11/24.
 */
public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

    private List<ArrayList> groups;

    public MyExpandableListViewAdapter(List<ArrayList> list) {
        this.groups = list;
    }

    // 设置组的个数
    @Override
    public int getGroupCount() {
        return groups.size();
    }

    // 设置孩子的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return groups.get(groupPosition).size();
    }

    // 根据组的位置获取的组的数据
    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return groups.get(groupPosition);
    }

    // 根据组的位置和孩子的位置获取孩子的数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return groups.get(groupPosition).get(childPosition);
    }

    // 获取组的id
    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    // 获取孩子的id
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    // 判断id是否稳定,如果你返回id,返回false,没有返回id,返回true
    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    // 设置组的样式
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        View inflate = ViewsUitls.inflate(R.layout.item_all_site_group);
        TextView siteName = (TextView) inflate.findViewById(R.id.tv_all_site_group_site_name);
        TextView siteWarnAllNumber = (TextView) inflate.findViewById(R.id.tv_all_site_group_site_all_number);

        return inflate;
    }

    // 设置孩子的样式
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        View inflate = ViewsUitls.inflate(R.layout.item_all_site_child);
        TextView monitorAddress = (TextView) inflate.findViewById(R.id.tv_all_site_child_monitor_address);
        TextView warnNumber = (TextView) inflate.findViewById(R.id.tv_all_site_child_monitor_warn_number);
        ImageView imageIcon = (ImageView) inflate.findViewById(R.id.iv_all_site_child_item_left_image);

        return inflate;
    }

    // 设置孩子是否可以点击,false:表示不可点击,true:表示可以点击
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

}
