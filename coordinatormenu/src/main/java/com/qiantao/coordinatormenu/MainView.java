package com.qiantao.coordinatormenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by qiantao on 2017/1/16.
 * 主界面
 */

public class MainView extends RelativeLayout {
    private CoordinatorMenu mCoordinatorMenu;

    public MainView(Context context) {
        this(context, null, 0);
    }

    public MainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void setParent(CoordinatorMenu coordinatorMenu) {
        mCoordinatorMenu = coordinatorMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mCoordinatorMenu.isOpened()) {
            return true;//拦截事件，不往下传递
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCoordinatorMenu.isOpened()) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mCoordinatorMenu.closeMenu();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }
}
