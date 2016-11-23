package com.minlu.fosterpig;

import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.fragment.AllSiteFragment;
import com.minlu.fosterpig.fragment.MainToWarnFragment;
import com.minlu.fosterpig.fragment.SureWarnFragment;


public class FragmentFactory {

    public static BaseFragment[] fragments = new BaseFragment[3];

    public static BaseFragment create(int position) {
        BaseFragment fragment = null;

        if (fragments[position] == null) {

            switch (position) {
                case 0:
                    fragment = new AllSiteFragment();
                    break;
                case 1:
                    fragment = new MainToWarnFragment();
                    break;
                case 2:
                    fragment = new SureWarnFragment();
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

