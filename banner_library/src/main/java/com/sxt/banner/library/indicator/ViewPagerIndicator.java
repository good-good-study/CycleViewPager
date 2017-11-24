package com.sxt.banner.library.indicator;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxt.banner.library.R;
import com.sxt.banner.library.util.Px2DpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by izhaohu on 2017/7/31.
 */

public class ViewPagerIndicator extends LinearLayout {
    private ViewPager viewPager;
    /**
     * tab显示的标题
     */
    private List<String> titles;
    /**
     * indicator的直接父View
     */
    private LinearLayout indicaorTitleLayout;
    /**
     * indicator指示线
     */
    private ViewGroup indicator;
    /**
     * indicator单次动画所持续的时长
     */
    private long duration = 0;
    /**
     * viewpager当前选中的position
     */
    private int lastPosition = -1;
    /**
     * indicator执行位移动画的目标位置
     */
    private int targetLocation;
    public List<Tab> tabList;
    /**
     * 标题排列方式 暂时只支持两种
     * <p>
     * 固定权重weight ： 1 - 1 - 1 - 1
     * 无权重 ： 居中显示
     */
    private int weightType;
    public static final int WEIGHT_TYPE_WEIGHT = 2;
    public static final int WEIGHT_TYPE_NO_WEIGHT = 1;
    private OnTabClickedListener onTabClickedListener;

    public ViewPagerIndicator(Context context) {
        super(context);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ViewPagerIndicator init() {
        //添加title布局
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_viewpager_indicator, null);
        indicaorTitleLayout = (LinearLayout) itemView.findViewById(R.id.viewpager_indicator_titile_root);
        indicator = (ViewGroup) itemView.findViewById(R.id.viewpager_indicator_line);
        addView(itemView);

        return this;
    }

    public ViewPagerIndicator setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateTextViewState(position);
                updateIncator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return this;
    }

    public ViewPager getViewPager() {
        return this.viewPager;
    }

    public ViewPagerIndicator setTitles(List<String> titles, int weightTpye) {
        this.titles = titles;
        this.weightType = weightTpye;
        addTitle();
        return this;
    }

    private void addTitle() {
        if (this.titles == null) return;
        tabList = new ArrayList<>();
        for (int i = 0; i < this.titles.size(); i++) {
            View tabItem = LayoutInflater.from(getContext()).inflate(R.layout.item_viewpager_tab, null);
            TextView title = (TextView) tabItem.findViewById(R.id.item_viewpager_indicator_tab_title);
            MsgView msgView = (MsgView) tabItem.findViewById(R.id.item_viewpager_indicator_tab_msg);
            title.setText(titles.get(i));
            Tab tab = new Tab(tabItem, title, msgView);
            title.setOnClickListener(new OnTabClickListener(tab, i, new OnTabClickedListener() {
                @Override
                public void onTabClicked(Tab tab, int position) {
                    setUnreadMsgAtIndexHide(position, true);
                    if (lastPosition != position) {
                        updateTextViewState(position);
                        updateIncator(position);
                        if (viewPager != null) viewPager.setCurrentItem(position);
                    }
                }
            }));
            tabList.add(tab);
            indicaorTitleLayout.addView(tabItem);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabItem.getLayoutParams();

            if (weightType == WEIGHT_TYPE_WEIGHT) {
                lp.width = 0;
                lp.weight = 1;
            } else {
                if (i == 1) lp.leftMargin = 50;
            }
            tabItem.setLayoutParams(lp);
        }
        this.post(new Runnable() {
            //此方法只调用一次 其原理是 將自定义的Runnable放入到消息队列的尾部 , 当Looper调用到它时 ,
            //View已经初始化完成了 , 所以在这里可以进行Indicator指示线的宽度初始化
            @Override
            public void run() {
                updateTextViewState(0);
                initIndicatorLocation();
            }
        });
    }

    @SuppressLint("ResourceType")
    public ViewPagerIndicator setIndicatorColorRes(int colorRes) {
        if (indicator != null) {
            indicator.setBackgroundColor(ContextCompat.getColor(getContext(), colorRes));
        }
        return this;
    }

    public ViewPagerIndicator setIndicatorHeight(int heightDpValue) {
        if (indicator != null) {
            LinearLayout.LayoutParams layoutParams = (LayoutParams) indicator.getLayoutParams();
            layoutParams.height = Px2DpUtil.dip2px(getContext(), heightDpValue);
        }
        return this;
    }

    public ViewPagerIndicator setIndicatorTranslateDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public Tab getCurrentSelectedTab() {
        if (viewPager != null && tabList != null && tabList.size() > 0) {
            return tabList.get(viewPager.getCurrentItem());
        }
        return null;
    }

    public int getCurrentSelectedTabIndex() {
        if (viewPager != null && tabList != null && tabList.size() > 0) {
            return viewPager.getCurrentItem();
        }
        return -1;
    }

    public ViewPagerIndicator setUnreadMsg(int tabIndex, int msgNum) {
        if (tabList != null && tabList.size() > 0 && tabIndex >= 0 && tabIndex < tabList.size()) {
            UnreadMsgUtils.show(tabList.get(tabIndex).getMsgView(), msgNum);
        }
        return this;
    }

    public ViewPagerIndicator setUnreadMsgAtIndexHide(int index, boolean isHide) {
        if (tabList != null && tabList.size() > 0 && index >= 0 && index < tabList.size()) {
            if (isHide) {
                tabList.get(index).getMsgView().setVisibility(INVISIBLE);
            } else {
                UnreadMsgUtils.show(tabList.get(index).getMsgView(), 0);
            }
        }
        return this;
    }

    public ViewPagerIndicator setUnreadMsgAllHideState(boolean isHide) {
        if (tabList != null && tabList.size() > 0) {
            for (int i = 0; i < tabList.size(); i++) {
                if (isHide) {
                    tabList.get(i).getMsgView().setVisibility(INVISIBLE);
                } else {
                    UnreadMsgUtils.show(tabList.get(i).getMsgView(), 0);
                }
            }
        }
        return this;
    }

    public int getTabContent() {
        return tabList == null ? 0 : tabList.size();
    }

    public List<Tab> getTabList() {
        return tabList;
    }

    private void updateTextViewState(int position) {
        if (tabList == null || tabList.size() == 0) return;
        for (int i = 0; i < tabList.size(); i++) {
            if (position == i) {
                tabList.get(i).getTitle().setTextColor(ContextCompat.getColor(getContext(), R.color.text_color_1));
            } else {
                tabList.get(i).getTitle().setTextColor(ContextCompat.getColor(getContext(), R.color.text_color_3));
            }
        }
    }

    private void updateIncator(int position) {
        if (tabList == null || tabList.size() == 0) return;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) indicator.getLayoutParams();
        lp.width = tabList.get(position).getTitle().getMeasuredWidth();
        indicator.setLayoutParams(lp);
        startAnimation(lastPosition, position);
        lastPosition = position;
        // 暂时先在点击后隐藏 红点
        setUnreadMsgAtIndexHide(position, true);
        if (onTabClickedListener != null)
            onTabClickedListener.onTabClicked(getCurrentSelectedTab(), position);
    }

    @SuppressLint("ObjectAnimatorBinding")
    private void startAnimation(int startPosition, int endPosition) {
        if (tabList == null || tabList.size() == 0) return;
        if (startPosition > endPosition) {//indicator 向左滑动
            for (int i = 0; i < titles.size(); i++) {
                if (endPosition == i) {
                    targetLocation = tabList.get(i).getTitleRoot().getLeft() + tabList.get(i).getTitle().getLeft();
                }
            }
        } else if (startPosition < endPosition) {//indicator 向右滑动
            for (int i = 0; i < titles.size(); i++) {
                if (endPosition == i) {
                    targetLocation = tabList.get(i).getTitleRoot().getRight() - tabList.get(i).getTitle().getRight();
                }
            }
        }
        ObjectAnimator
                .ofFloat(indicator, "translationX", targetLocation)
                .setDuration(duration <= 0 ? 300 : duration)
                .start();
    }


    @SuppressLint("ResourceType")
    private void initIndicatorLocation() {
        if (tabList == null || tabList.size() == 0) return;
        targetLocation = tabList.get(0).getTitleRoot().getRight() - tabList.get(0).getTitle().getRight();
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(indicator, "translationX", targetLocation)
                .setDuration(5);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                indicator.setVisibility(VISIBLE);
                LayoutParams lp = (LayoutParams) indicator.getLayoutParams();
                lp.width = tabList.get(0).getTitle().getMeasuredWidth();
                indicator.setLayoutParams(lp);
                startAnimation(lastPosition, 0);
                lastPosition = 0;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    public ViewPagerIndicator setOnTabClickedListener(OnTabClickedListener onTabClickedListener) {
        this.onTabClickedListener = onTabClickedListener;
        return this;
    }

}
