package com.sxt.banner.library.indicator;

import android.view.View;

/**
 * Created by izhaohu on 2017/11/20.
 */

public class OnTabClickListener implements View.OnClickListener {

    private Tab tab;
    private int position;
    private OnTabClickedListener onTabClickedListener;

    public OnTabClickListener(Tab tab, int position, OnTabClickedListener onTabClickedListener) {
        this.tab = tab;
        this.position = position;
        this.onTabClickedListener = onTabClickedListener;
    }

    @Override
    public void onClick(View view) {
        if (onTabClickedListener != null) {
            onTabClickedListener.onTabClicked(tab, position);
        }
    }

}
