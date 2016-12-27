package com.minlu.fosterpig.fragment;

import android.view.View;
import android.widget.ExpandableListView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.bean.VideoBean;
import com.minlu.fosterpig.util.ViewsUitls;

import java.util.List;

/**
 * Created by user on 2016/12/27.
 */

public class VideoFragment extends BaseFragment<VideoBean> {

    private List<VideoBean> mVideoData;
    private ExpandableListView expandableListView;
    private int currentExpandGroup = -1;

    @Override
    protected void onSubClassOnCreateView() {
        loadDataAndRefresh();
    }

    @Override
    protected View onCreateSuccessView() {

        View inflate = ViewsUitls.inflate(R.layout.layout_video);

        expandableListView = (ExpandableListView) inflate.findViewById(R.id.elv_video);
        expandableListView.setGroupIndicator(null);

//        VideoAdapter videoAdapter = new VideoAdapter(mVideoData);
//        expandableListView.setAdapter(videoAdapter);

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
        return chat(mVideoData);
    }
}
