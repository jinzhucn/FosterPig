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
public class SureWarnHolder extends BaseHolder<String> {

    private ImageView mLeftImage;
    private TextView mMonitorAddress;
    private TextView mMonitorWarnTime;
    private TextView mMonitorWarnNumber;
    private TextView mSureWarnTime;

    @Override
    public View initView() {

        View inflate = ViewsUitls.inflate(R.layout.item_sure_warn_list_view);

        mLeftImage = (ImageView) inflate.findViewById(R.id.iv_item_sure_warn_left_image);
        mMonitorAddress = (TextView) inflate.findViewById(R.id.tv_sure_warn_monitor_address);
        mMonitorWarnTime = (TextView) inflate.findViewById(R.id.tv_sure_warn_monitor_warn_time);
        mMonitorWarnNumber = (TextView) inflate.findViewById(R.id.tv_sure_warn_monitor_warn_number);
        mSureWarnTime = (TextView) inflate.findViewById(R.id.tv_sure_warn_time);

        return inflate;
    }

    @Override
    public void setRelfshData(String mData, int postion) {

        mMonitorWarnNumber.setText(postion + "");
    }
}
