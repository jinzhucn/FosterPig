package com.minlu.fosterpig.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.base.BaseHolder;
import com.minlu.fosterpig.util.ViewsUitls;

/**
 * Created by user on 2016/11/22.
 */
public class WarnHolder extends BaseHolder<String> {

    private ImageView mLeftImage;
    private TextView mMonitorAddress;
    private TextView mMonitorWarnTime;
    private TextView mMonitorWarnNumber;

    @Override
    public View initView() {

        View inflate = ViewsUitls.inflate(R.layout.item_swipe_menu_list_view);

        mLeftImage = (ImageView) inflate.findViewById(R.id.iv_item_left_image);
        mMonitorAddress = (TextView) inflate.findViewById(R.id.tv_monitor_address);
        mMonitorWarnTime = (TextView) inflate.findViewById(R.id.tv_monitor_warn_time);
        mMonitorWarnNumber = (TextView) inflate.findViewById(R.id.tv_monitor_warn_number);

        return inflate;
    }

    @Override
    public void setRelfshData(String mData, int postion) {


        if(postion==1){
            mLeftImage.setImageResource(R.mipmap.small_icon_warn_ammonia);
            mMonitorAddress.setText("kzgfhaqosdhlas");
            mMonitorWarnTime.setText("mnjbhiuhoij;m");
        }else if(postion%2==0){
            mLeftImage.setImageResource(R.mipmap.small_icon_warn_temperature);
            mMonitorAddress.setText("kzgfhukywuieyriuaqosdhlas");
            mMonitorWarnTime.setText("mnjbhiuhvnbdfgasdcoij;m");
        }else if(postion%3==0){
            mLeftImage.setImageResource(R.mipmap.small_icon_warn_humidity);
            mMonitorAddress.setText("kzs");
            mMonitorWarnTime.setText("mnjbhm");
        }else if(postion%5==0){
            mLeftImage.setImageResource(R.mipmap.small_icon_warn_ammonia);
            mMonitorAddress.setText("1231231231");
            mMonitorWarnTime.setText("45734548");
        }








        mMonitorWarnNumber.setText(mData);

    }
}
