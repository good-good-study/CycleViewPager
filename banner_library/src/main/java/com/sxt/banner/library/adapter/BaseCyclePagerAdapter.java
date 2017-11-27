package com.sxt.banner.library.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by sxt on 2017/7/29.
 */
public abstract class BaseCyclePagerAdapter<T> extends PagerAdapter {
    protected Context context;
    public List<T> datas;

    public BaseCyclePagerAdapter(Context context, List<T> datas) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size() + 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int datePosition;
        if (position == 0) {
            datePosition = this.datas.size() - 1;
        } else if (position == getCount() - 1) {
            datePosition = 0;
        } else {
            datePosition = --position;
        }
        return instantiateItem(container, position, datePosition);
    }

    public Object instantiateItem(ViewGroup container, int position, int datePosition) {
        return super.instantiateItem(container, position);
    }
}
