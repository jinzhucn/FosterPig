package com.minlu.fosterpig.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.minlu.fosterpig.FragmentFactory;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.fragment.AllSiteFragment;
import com.minlu.fosterpig.fragment.MainToWarnFragment;
import com.minlu.fosterpig.fragment.SureWarnFragment;
import com.minlu.fosterpig.util.ViewsUitls;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by user on 2016/11/23.
 */
public class TrueTimeDataActivity extends BaseActivity implements View.OnClickListener {

    private TextView mAllSite;
    private TextView mWarnInformation;
    private TextView mSureWarn;
    private List<TextView> mTextViews;
    private AllSiteFragment mAllSiteFragment;
    private MainToWarnFragment mMainToWarnFragment;
    private SureWarnFragment mSureWarnFragment;

    @Override
    public void onCreateContent() {

        getThreeLine().setVisibility(View.GONE);
        setBackVisibility(View.VISIBLE);
        setSettingVisibility(View.GONE);

        View view = setContent(R.layout.activity_true_time);

        initContentView(view);

        showFragmentWhenFirstOrSecond();
    }


    /*
   *   此方法中对第一次进入界面直接展示一个Fragment
   *   当activity销毁时且数据保存了，那么则从FragmentManger中取出一存储的Fragment
   * */
    private void showFragmentWhenFirstOrSecond() {
        if (savedInstanceState == null) {// 第一次进入该界面，需要通过add来进行展示Fragment
            System.out.println("第一次进入该界面，需要通过add来进行展示Fragment");
            // mFromFragment为空的情况下
            amendClickStyle(StringsFiled.SELECT_ALL_SITE_TAB, 1, "");
        } else {
            System.out.println("第二次+++++++++++++++++++++++++++++");
            mAllSiteFragment = (AllSiteFragment) getSupportFragmentManager().findFragmentByTag(StringsFiled.TAG_OPEN_ALL_SITE_FRAGMENT);
            mMainToWarnFragment = (MainToWarnFragment) getSupportFragmentManager().findFragmentByTag(StringsFiled.TAG_OPEN_WARN_INFORMATION_FRAGMENT);
            mSureWarnFragment = (SureWarnFragment) getSupportFragmentManager().findFragmentByTag(StringsFiled.TAG_OPEN_SURE_WARN_FRAGMENT);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            // 所有站点的Tab对应的Fragment必须显示
            if (mAllSiteFragment != null) {
                fragmentTransaction.show(mAllSiteFragment);
            } else {
                mAllSiteFragment = new AllSiteFragment();
                fragmentTransaction.add(R.id.fl_select_true_time, mAllSiteFragment, StringsFiled.TAG_OPEN_ALL_SITE_FRAGMENT);
            }
            // 走完上面的if else判断后mAllSiteFragment必定有值
            FragmentFactory.fragments[0] = mAllSiteFragment;
            mFromFragment = mAllSiteFragment;
            alreadyPress = 1; // 防止再次点击所有站点的Tab


            // 下面两个TAB对应的Fragment必须隐藏起来
            if (mMainToWarnFragment != null) {
                FragmentFactory.fragments[1] = mMainToWarnFragment;
                fragmentTransaction.hide(mMainToWarnFragment);
            }
            if (mSureWarnFragment != null) {
                FragmentFactory.fragments[2] = mSureWarnFragment;
                fragmentTransaction.hide(mSureWarnFragment);
            }

            fragmentTransaction.commit();
        }
    }


    private void initContentView(View view) {
        mTextViews = new ArrayList<>();

        mAllSite = (TextView) view.findViewById(R.id.tv_tab_all_site);
        mAllSite.setOnClickListener(this);
        mTextViews.add(mAllSite);
        mWarnInformation = (TextView) view.findViewById(R.id.tv_tab_warn_information);
        mWarnInformation.setOnClickListener(this);
        mTextViews.add(mWarnInformation);
        mSureWarn = (TextView) view.findViewById(R.id.tv_tab_sure_warn);
        mSureWarn.setOnClickListener(this);
        mTextViews.add(mSureWarn);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_tab_all_site:
                amendClickStyle(StringsFiled.SELECT_ALL_SITE_TAB, 1, StringsFiled.TAG_OPEN_ALL_SITE_FRAGMENT);
                break;
            case R.id.tv_tab_warn_information:
                amendClickStyle(StringsFiled.SELECT_WARN_INFORMATION_TAB, 2, StringsFiled.TAG_OPEN_WARN_INFORMATION_FRAGMENT);
                break;
            case R.id.tv_tab_sure_warn:
                amendClickStyle(StringsFiled.SELECT_SURE_WARN_INFORMATION_TAB, 3, StringsFiled.TAG_OPEN_SURE_WARN_FRAGMENT);
                break;
        }

    }


    private int alreadyPress = 0;

    /**
     * click 为被点击的选项卡
     */
    private void amendClickStyle(int bundleValue, int toFragment, String tag) {

        if (toFragment != alreadyPress) {
            alreadyPress = toFragment;
            for (int i = 1; i < mTextViews.size() + 1; i++) {
                TextView view = mTextViews.get(i - 1);
                if (i == toFragment) {
                    view.setTextColor(ContextCompat.getColor(ViewsUitls.getContext(), R.color.thin_blue));
                    view.setBackgroundResource(R.drawable.shape_select_tap_background_white);
                    view.setTextAppearance(ViewsUitls.getContext(), R.style.text_blod);
                } else {
                    view.setTextColor(ContextCompat.getColor(ViewsUitls.getContext(), R.color.white));
                    view.setBackgroundResource(R.drawable.shape_select_tap_background_blue);
                    view.setTextAppearance(ViewsUitls.getContext(), R.style.text_normal);
                }
            }
            startSelectAreaContent(bundleValue, toFragment, tag);
        }

    }

    /*被选中的Fragment*/
    private BaseFragment mFromFragment = null;

    // 开启具体楼层的Fragment
    /*
    * selectTab：要传送的数据
    * toFragment：要显示的Fragment
    * tag：addFragment时添加的标记
    *
    * */
    private void startSelectAreaContent(int bundleValue, int toFragment, String tag) {

        BaseFragment baseFragment = FragmentFactory.create(toFragment - 1);

        if (mFromFragment != baseFragment) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (!baseFragment.isAdded()) {    // 先判断是否被add过
                // 设置要传送的数据
                Bundle bundle = new Bundle();
                bundle.putInt(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY, bundleValue);
                baseFragment.setArguments(bundle);

                if (mFromFragment == null) {
                    transaction.add(R.id.fl_select_true_time, baseFragment, tag).commit(); // 第一次进入本页面，直接add到Activity中
                } else {
                    transaction.hide(mFromFragment).add(R.id.fl_select_true_time, baseFragment, tag).commit(); // 隐藏当前的fragment，add下一个到Activity中
                }

            } else {
                transaction.hide(mFromFragment).show(baseFragment).commit(); // 隐藏当前的fragment，显示下一个
            }

            mFromFragment = baseFragment;
        }
    }


}
