package com.qiantao.coordinatormenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by qiantao on 2017/1/10.
 * 仿QQ的协同侧滑菜单
 */

public class CoordinatorMenu extends FrameLayout {

    private static final String TAG = "DragViewGroup";
    private final int mScreenWidth;
    private final int mScreenHeight;

    private View mMenuView;
    private MainView mMainView;

    private ViewDragHelper mViewDragHelper;

    private static final int MENU_CLOSED = 1;
    private static final int MENU_OPENED = 2;
    private int mMenuState = MENU_CLOSED;

    private int mDragOrientation;
    private static final int LEFT_TO_RIGHT = 3;
    private static final int RIGHT_TO_LEFT = 4;

    private static final float SPRING_BACK_VELOCITY = 1500;
    private static final int SPRING_BACK_DISTANCE = 80;
    private int mSpringBackDistance;

    private static final int MENU_MARGIN_RIGHT = 64;
    private int mMenuWidth;

    private static final int MENU_OFFSET = 128;
    private int mMenuOffset;

    private static final float TOUCH_SLOP_SENSITIVITY = 1.f;

    private static final String DEFAULT_SHADOW_OPACITY = "00";
    private String mShadowOpacity = DEFAULT_SHADOW_OPACITY;

    public CoordinatorMenu(Context context) {
        this(context, null);
    }

    public CoordinatorMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoordinatorMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final float density = getResources().getDisplayMetrics().density;//屏幕密度

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        mSpringBackDistance = (int) (SPRING_BACK_DISTANCE * density + 0.5f);

        mMenuOffset = (int) (MENU_OFFSET * density + 0.5f);

        mMenuWidth = mScreenWidth - (int) (MENU_MARGIN_RIGHT * density + 0.5f);

        mViewDragHelper = ViewDragHelper.create(this, TOUCH_SLOP_SENSITIVITY, new CoordinatorCallback());

    }

    private class CoordinatorCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return mMainView == child || mMenuView == child;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            if (capturedChild == mMenuView) {
                mViewDragHelper.captureChildView(mMainView, activePointerId);
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (left < 0) {
                left = 0;
            } else if (left > mMenuWidth) {
                left = mMenuWidth;
            }
            return left;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            Log.e(TAG, "onViewReleased: xvel: " + xvel);
            if (mDragOrientation == LEFT_TO_RIGHT) {
                if (xvel > SPRING_BACK_VELOCITY || mMainView.getLeft() > mSpringBackDistance) {
                    openMenu();
                } else {
                    closeMenu();
                }
            } else if (mDragOrientation == RIGHT_TO_LEFT) {
                if (xvel < -SPRING_BACK_VELOCITY || mMainView.getLeft() < mMenuWidth - mSpringBackDistance) {
                    closeMenu();
                } else {
                    openMenu();
                }
            }

        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            Log.d(TAG, "onViewPositionChanged: dx:" + dx);
            if (dx > 0) {
                mDragOrientation = LEFT_TO_RIGHT;
            } else if (dx < 0) {
                mDragOrientation = RIGHT_TO_LEFT;
            }
            float scale = (float) (mMenuWidth - mMenuOffset) / (float) mMenuWidth;
            int menuLeft = left - ((int) (scale * left) + mMenuOffset);
            mMenuView.layout(menuLeft, mMenuView.getTop(),
                    menuLeft + mMenuWidth, mMenuView.getBottom());
            float showing = (float) (mScreenWidth - left) / (float) mScreenWidth;
            int hex = 255 - Math.round(showing * 255);
            if (hex < 16) {
                mShadowOpacity = "0" + Integer.toHexString(hex);
            } else {
                mShadowOpacity = Integer.toHexString(hex);
            }
        }
    }

    //加载完布局文件后调用
    @Override
    protected void onFinishInflate() {
        mMenuView = getChildAt(0);
        mMainView = (MainView) getChildAt(1);
        mMainView.setParent(this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mViewDragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将触摸事件传递给ViewDragHelper，此操作必不可少
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "onLayout: ");
        super.onLayout(changed, left, top, right, bottom);
        MarginLayoutParams menuParams = (MarginLayoutParams) mMenuView.getLayoutParams();
        menuParams.width = mMenuWidth;
        mMenuView.setLayoutParams(menuParams);
        if (mMenuState == MENU_OPENED) {
            mMenuView.layout(0, 0, mMenuWidth, bottom);
            mMainView.layout(mMenuWidth, 0, mMenuWidth + mScreenWidth, bottom);
            return;
        }
        mMenuView.layout(-mMenuOffset, top, mMenuWidth - mMenuOffset, bottom);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final int restoreCount = canvas.save();//保存画布当前的剪裁信息

        final int height = getHeight();
        final int clipLeft = 0;
        int clipRight = mMainView.getLeft();
        if (child == mMenuView) {
            canvas.clipRect(clipLeft, 0, clipRight, height);//剪裁显示的区域
        }

        boolean result = super.drawChild(canvas, child, drawingTime);//绘制当前view

        //恢复画布之前保存的剪裁信息
        //以正常绘制之后的view
        canvas.restoreToCount(restoreCount);


        int shadowLeft = mMainView.getLeft();
        Log.d(TAG, "drawChild: shadowLeft: " + shadowLeft);
        final Paint shadowPaint = new Paint();
        Log.d(TAG, "drawChild: mShadowOpacity: " + mShadowOpacity);
        shadowPaint.setColor(Color.parseColor("#" + mShadowOpacity + "777777"));
        shadowPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(shadowLeft, 0, mScreenWidth, mScreenHeight, shadowPaint);

        return result;
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        if (mMainView.getLeft() == 0) {
            mMenuState = MENU_CLOSED;
        } else if (mMainView.getLeft() == mMenuWidth) {
            mMenuState = MENU_OPENED;
        }
    }

    public void openMenu() {
        mViewDragHelper.smoothSlideViewTo(mMainView, mMenuWidth, 0);
        ViewCompat.postInvalidateOnAnimation(CoordinatorMenu.this);
    }

    public void closeMenu() {
        mViewDragHelper.smoothSlideViewTo(mMainView, 0, 0);
        ViewCompat.postInvalidateOnAnimation(CoordinatorMenu.this);
    }

    public boolean isOpened() {
        return mMenuState == MENU_OPENED;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final CoordinatorMenu.SavedState ss = new CoordinatorMenu.SavedState(superState);
        ss.menuState = mMenuState;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof CoordinatorMenu.SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final CoordinatorMenu.SavedState ss = (CoordinatorMenu.SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (ss.menuState == MENU_OPENED) {
            openMenu();
        }
    }

    protected static class SavedState extends AbsSavedState {
        int menuState;

        SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            menuState = in.readInt();
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(menuState);
        }

        public static final Creator<CoordinatorMenu.SavedState> CREATOR = ParcelableCompat.newCreator(
                new ParcelableCompatCreatorCallbacks<CoordinatorMenu.SavedState>() {
                    @Override
                    public CoordinatorMenu.SavedState createFromParcel(Parcel in, ClassLoader loader) {
                        return new CoordinatorMenu.SavedState(in, loader);
                    }

                    @Override
                    public CoordinatorMenu.SavedState[] newArray(int size) {
                        return new CoordinatorMenu.SavedState[size];
                    }
                });
    }
}