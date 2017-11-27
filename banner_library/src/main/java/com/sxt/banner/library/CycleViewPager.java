package com.sxt.banner.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sxt.banner.library.adapter.BaseCyclePagerAdapter;


/**
 * Created by dell1 on 2017/9/3.
 */

public class CycleViewPager extends FrameLayout {

    private ViewPager viewPager;
    private BaseCyclePagerAdapter adapter;
    private LinearLayout pointContainer;
    private int checkedRes;
    private int unCheckedRes;
    private int currentItem = 0;
    private int marginLeft = 10;
    private final int STOP_SCROLL = 0;
    private final int START_SCROLL = 1;
    private long duration = 3000;
    private OnPageSelectedListener onPageSelectedListener;
    private boolean isOpenSelfScroll = false;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case START_SCROLL://开始切换
                    currentItem++;
                    viewPager.setCurrentItem(currentItem % adapter.getCount());
                    startScroll();
                    break;
                case STOP_SCROLL://停止切换
                    stopScroll();
                    break;
            }
        }
    };

    public CycleViewPager(@NonNull Context context) {
        super(context);
        init();
    }

    public CycleViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CycleViewPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //添加viewpager
        viewPager = new ViewPager(getContext());
        viewPager.setOverScrollMode(SCROLL_AXIS_NONE);
        viewPager.setFocusable(false);
        addView(viewPager);
        LayoutParams lp0 = (LayoutParams) viewPager.getLayoutParams();
        lp0.gravity = Gravity.CENTER;
        lp0.width = LayoutParams.MATCH_PARENT;
        lp0.height = LayoutParams.MATCH_PARENT;
        viewPager.setLayoutParams(lp0);
        //添加指示点根布局
        pointContainer = new LinearLayout(getContext());
        addView(pointContainer);
        LayoutParams lp1 = (LayoutParams) pointContainer.getLayoutParams();
        pointContainer.setPadding(0, 2, 0, 2);
        lp1.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        lp1.bottomMargin = 20;
        lp1.width = LayoutParams.WRAP_CONTENT;
        lp1.height = LayoutParams.WRAP_CONTENT;
        pointContainer.setLayoutParams(lp1);

        checkedRes = R.drawable.check;
        unCheckedRes = R.drawable.uncheck;
    }

    @SuppressLint("ClickableViewAccessibility")
    public CycleViewPager setAdapter(final BaseCyclePagerAdapter adapter) {
        if (viewPager != null && adapter != null) {
            this.adapter = adapter;
            viewPager.setAdapter(adapter);
            updatePoint(0);
            viewPager.setCurrentItem(1);
            viewPager.addOnPageChangeListener(new ViewPagerOnPageChangeListener());
        }
        return this;
    }

    float downX = 0;
    float moveX = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                if (Math.abs(moveX - downX) > 5) {
                    stopScroll();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isOpenSelfScroll) {
                    startScroll();
                }
                downX = 0;
                moveX = 0;
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public CycleViewPager setPointRes(int checkedRes, int unCheckedRes) {
        this.checkedRes = checkedRes;
        this.unCheckedRes = unCheckedRes;
        return this;
    }

    public CycleViewPager setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
        if (currentItem >= 0 && viewPager != null && adapter != null && adapter.getCount() > 0)
            viewPager.setCurrentItem(currentItem % adapter.getCount());
        return this;
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public CycleViewPager setScrollSelfState(boolean isOpenSelfScroll) {
        this.isOpenSelfScroll = isOpenSelfScroll;
        return this;
    }

    public CycleViewPager updatePoint(int position) {

        pointContainer.removeAllViews();
        for (int i = 0; i < adapter.getCount() - 2; i++) {
            ImageView img = new ImageView(getContext());
            if (position == i) {
                img.setImageResource(checkedRes);
            } else {
                img.setImageResource(unCheckedRes);
            }

            pointContainer.addView(img);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) img.getLayoutParams();
            lp.leftMargin = marginLeft;
            lp.gravity = Gravity.CENTER_VERTICAL;
            img.setLayoutParams(lp);
        }
        return this;
    }

    public CycleViewPager setPointMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public CycleViewPager setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public CycleViewPager addOnPageSelecedListener(OnPageSelectedListener onPageSelectedListener) {
        this.onPageSelectedListener = onPageSelectedListener;
        return this;
    }

    public interface OnPageSelectedListener {
        /**
         * @param viewPager    当前的ViewPager
         * @param adapter      当前的ViewPager的适配器
         * @param position     实际滑动后的position
         * @param fixPosition  修复后的position,仅用于viewPager无限循环
         * @param datePosition 真实数据的osition & indicator的指示位置
         */
        void onPageSelected(ViewPager viewPager, BaseCyclePagerAdapter adapter, int position, int fixPosition, int datePosition);
    }

    private class ViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        int fixPosition = -1;
        int datePosition;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (onPageSelectedListener != null) {
                if (position == 0) {
                    fixPosition = adapter.getCount() - 2;
                    datePosition = adapter.getCount() - 3;
                } else if (position == adapter.getCount() - 1) {
                    fixPosition = 1;
                    datePosition = 0;
                } else {
                    fixPosition = position;
                    datePosition = position - 1;
                }
                updatePoint(datePosition);
                currentItem = fixPosition;
                onPageSelectedListener.onPageSelected(viewPager, adapter, position, fixPosition, datePosition);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE && fixPosition != -1) {
                viewPager.setCurrentItem(fixPosition, false);
            }
        }
    }

    public void startScroll() {
        if (isOpenSelfScroll && handler != null) {
            handler.sendEmptyMessageDelayed(START_SCROLL, duration);
        }
    }

    public void stopScroll() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            stopScroll();
        } else {
            startScroll();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null) {
            stopScroll();
        }
    }

}
