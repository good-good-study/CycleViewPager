package com.sxt.banner.library;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by sxt on 2017/7/29.
 */
public abstract class BaseCycleViewPagerAdapter<T> extends PagerAdapter {
    protected Context context;
    public List<T> datas;

    public BaseCycleViewPagerAdapter(Context context, List<T> datas) {
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
        int fixPosition;
        if (position == 0) {
            fixPosition = datas.size() - 1;
        } else if (position == getCount() - 1) {
            fixPosition = 0;
        } else {
            fixPosition = position - 1;
        }
        return instantiateItem(container, position, fixPosition);
    }

    public Object instantiateItem(ViewGroup container, int position, int fixPosition) {
        return super.instantiateItem(container, position);
    }
}
