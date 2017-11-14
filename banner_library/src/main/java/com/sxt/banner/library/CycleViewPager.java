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


/**
 * Created by dell1 on 2017/9/3.
 */

public class CycleViewPager extends FrameLayout {

    private ViewPager viewPager;
    private BaseCycleViewPagerAdapter adapter;
    private LinearLayout pointContainer;
    private int checkedRes;
    private int unCheckedRes;
    private int currentItem = 0;
    private int marginLeft = 8;
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
                    if (isOpenSelfScroll) {
                        sendEmptyMessageDelayed(START_SCROLL, duration);
                    }
                    break;
                case STOP_SCROLL://停止切换
                    removeMessages(START_SCROLL);
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
    public CycleViewPager setAdapter(final BaseCycleViewPagerAdapter adapter) {
        if (viewPager != null && adapter != null) {
            this.adapter = adapter;
            viewPager.setAdapter(adapter);
            checkPoint(currentItem);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                int currentIndex = 0;

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentIndex = position;
                    currentItem = currentIndex;
                    if (onPageSelectedListener != null) {
                        int fixPosition;
                        if (position == 0) {
                            fixPosition = adapter.datas.size() - 1;
                        } else if (position == adapter.getCount() - 1) {
                            fixPosition = 0;
                        } else {
                            fixPosition = position - 1;
                        }
                        checkPoint(fixPosition);
                        onPageSelectedListener.onPageSelected(viewPager, adapter, fixPosition);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        if (currentIndex == 0) {
                            currentIndex = adapter.getCount() - 2;
                        } else if (currentIndex == adapter.getCount() - 1) {
                            currentIndex = 1;
                        }
                        viewPager.setCurrentItem(currentIndex, false);
                    }
                }
            });
        }
        return this;
    }

    float downX = 0;
    float moveX = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                if (isOpenSelfScroll && Math.abs(moveX - downX) > 5) {
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
        return super.onTouchEvent(event);
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

    public CycleViewPager checkPoint(int position) {
        if (viewPager == null || viewPager.getAdapter() == null || viewPager.getAdapter().getCount() == 0)
            return this;
        if (position < 0) position = 0;
        if (position >= viewPager.getAdapter().getCount() - 2)
            position = viewPager.getAdapter().getCount() - 3;

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

    public void addOnPageSelecedListener(OnPageSelectedListener onPageSelectedListener) {
        this.onPageSelectedListener = onPageSelectedListener;
    }

    public interface OnPageSelectedListener {
        void onPageSelected(ViewPager viewPager, BaseCycleViewPagerAdapter adapter, int position);
    }

    public void startScroll() {
        if (isOpenSelfScroll && viewPager != null && viewPager.getAdapter() != null && viewPager.getAdapter().getCount() > 0) {
            handler.sendEmptyMessageDelayed(START_SCROLL, duration);
        }
    }

    public void stopScroll() {
        handler.removeCallbacksAndMessages(null);
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
