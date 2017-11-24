package com.sxt.banner.library.indicator;

import android.view.View;
import android.widget.TextView;

public class Tab {
    public View titleRoot;
    public TextView title;
    public MsgView msgView;

    public Tab(View titleRoot, TextView title, MsgView msgView) {
        this.titleRoot = titleRoot;
        this.title = title;
        this.msgView = msgView;
    }

    public View getTitleRoot() {
        return titleRoot;
    }

    public void setTitleRoot(View titleRoot) {
        this.titleRoot = titleRoot;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public MsgView getMsgView() {
        return msgView;
    }

    public void setMsgView(MsgView msgView) {
        this.msgView = msgView;
    }
}