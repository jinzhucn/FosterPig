package com.minlu.fosterpig;

import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.fragment.AllSiteFragment;
import com.minlu.fosterpig.fragment.AllWarnFragment;
import com.minlu.fosterpig.fragment.MainToAlreadyWarnFragment;
import com.minlu.fosterpig.fragment.MainToWarnFragment;
import com.minlu.fosterpig.fragment.SureWarnFragment;


public class FragmentFactory {

    /*存储工长要造的对象的仓库*/
    public static BaseFragment[] fragments = new BaseFragment[5];

    public static BaseFragment create(int position) {
        BaseFragment fragment = null;

        /*仓库里对应位置的对象为空才需要重新创造*/
        if (fragments[position] == null) {

            switch (position) {
                case 0:
                    fragment = new AllSiteFragment();
                    break;
                case 1:
                    fragment = new AllWarnFragment();
                    break;
                case 2:
                    fragment = new SureWarnFragment();
                    break;
                case 3:
                    fragment = new MainToWarnFragment();
                    break;
                case 4:
                    fragment = new MainToAlreadyWarnFragment();
                    break;
            }

            fragments[position] = fragment;

            return fragment;
        } else {
            System.out.println("已经存在");
            return fragments[position];
        }
    }

}

