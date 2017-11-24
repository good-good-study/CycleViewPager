package com.sxt.banner.library.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by izhaohu on 2017/10/12.
 */

public class LazyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private String[] titles;
    private Context context;
    private List<Fragment> fragments;

    public LazyFragmentPagerAdapter(FragmentManager fm, Context context, List<Fragment> fragments, String[] titles) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return titles[position];
//    }

    public void notifyDataSetChanged(List<Fragment> fragments) {
        if (fragments != null) {
            this.fragments = fragments;
            notifyDataSetChanged();
        }
    }
}
