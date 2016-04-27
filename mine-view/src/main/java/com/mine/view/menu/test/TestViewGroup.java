package com.mine.view.menu.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.ext.R;
import android.widget.FrameLayout;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gang on 16-4-18.
 */
public class TestViewGroup extends ViewGroup {

    private static final String TAG = "test_viewgroup";
    private List<SlideSectionMenuItem> mMenuItems = new ArrayList<>();
    private Scroller mScroller;
    //当前的状态
    private State mMenuState = State.CLOSED;
    private Gravity mGravity = Gravity.LEFT_TOP;

    private enum State {
        OPENED, OPENING, CLOSING, CLOSED
    }

    public enum Gravity {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, TIGHT_BOTTOM
    }

    public TestViewGroup(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public TestViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public TestViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
        mScroller = new Scroller(context, new OvershootInterpolator());
        setWillNotDraw(true);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TestViewGroup, defStyle, 0);
            int index = a.getInt(R.styleable.TestViewGroup_gravity, -1);
            if (index >= 0) {

                switch (index) {
                    case 1: {
                        setGravity(Gravity.LEFT_TOP);
                        break;
                    }
                    case 2: {
                        setGravity(Gravity.LEFT_BOTTOM);
                        break;
                    }
                    case 3: {
                        setGravity(Gravity.RIGHT_TOP);
                        break;
                    }
                    case 4: {
                        break;
                    }
                }
            }
        }
    }

    public void openMenu() {
        if (mMenuState == State.CLOSED) {
            mMenuState = State.OPENING;
            //open
            mScroller.startScroll(0, 0, 0, getMeasuredHeight(), 500);
            postInvalidate();
            Log.d(TAG, "open menu:0-" + getMeasuredHeight());
        }
    }

    public void closeMenu() {
        if (mMenuState == State.OPENED) {
            mMenuState = State.CLOSING;
            //close
            mScroller.startScroll(0, getMeasuredHeight(), 0, 0, 500);
            postInvalidate();
            Log.d(TAG, "close menu:" + getMeasuredHeight() + "-0");
        }
    }

    public void toggleMenu() {
        switch (mMenuState) {
            case OPENED: {
                closeMenu();
                break;
            }
            case CLOSED: {
                openMenu();
                break;
            }
        }
    }

    public void setGravity(Gravity gravity) {
        if (mGravity != gravity) {
            mGravity = gravity;
            requestLayout();
        }
    }

    public void addItem(SlideSectionMenuItem item) {
        if (item != null) {
            mMenuItems.add(item);
        }
    }

    public void setItems(List<SlideSectionMenuItem> menuItems) {
        if (menuItems != null) {
            mMenuItems.clear();
            mMenuItems.addAll(menuItems);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (!mScroller.isFinished()) {
            final int y = mScroller.getCurrY();
            int bottomHeight = 0;
            Log.d(TAG, "computeScroll:" + y);
            switch (mMenuState) {
                case OPENING: {
                    if (mScroller.computeScrollOffset()) {
                        for (int i = 0; i < getChildCount(); i++) {
                            final View child = getChildAt(i);
                            final int childH = child.getMeasuredHeight();
                            if (i == 0) {
                                setTranslationY(-y);
                                if (y > childH) {
                                    bottomHeight = y - childH;
                                }
                            } else {
                                setTranslationY(-bottomHeight);
                                if (bottomHeight > childH) {
                                    bottomHeight -= childH;
                                }
                            }
                        }
                    }
                    break;
                }
                case CLOSING: {
                    if (mScroller.computeScrollOffset()) {
                        for (int i = 0; i < getChildCount(); i++) {
                            final View child = getChildAt(i);
                            final int childH = child.getMeasuredHeight();//margin
                            if (i == 0) {
                                child.setTranslationY(y);
                                if (y > childH) {
                                    bottomHeight = y - childH;
                                }
                            } else {
                                child.setTranslationY(bottomHeight);
                                if (bottomHeight > childH) {
                                    bottomHeight -= childH;
                                }
                            }
                        }
                    }
                    break;
                }
            }
            postInvalidate();
        } else {
            switch (mMenuState) {
                case OPENING: {
                    Log.d(TAG, " opened ");
                    mMenuState = State.OPENED;
                    break;
                }
                case CLOSING: {
                    Log.d(TAG, " closed ");
                    mMenuState = State.CLOSED;
                    break;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        final int widthExtra = paddingLeft + paddingRight;
        final int heightExtra = paddingTop + paddingBottom;

        final int childCount = getChildCount();

        //只测量子view的,不测量本viewGroup
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            int widthSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
            int heightSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST);
            measureChildWithMargins(child, widthSpec, widthExtra, heightSpec, heightExtra);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        int baseTopLine = t + paddingTop;

        Log.d(TAG, "===  onLayout self ===left:" + l + ",top:" + t + ",right:" + r + ",bottom:" + b);


        //从上向下布局
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);

            final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            final int height = child.getMeasuredHeight();
            final int width = child.getMeasuredWidth();

            baseTopLine += lp.topMargin;

            final int left = l;
            final int right = left + width;

            final int top = baseTopLine;
            final int bottom = top + height;

            baseTopLine += height + lp.topMargin;

            child.layout(left, top, right, bottom);
            Log.d(TAG, " onLayout child:left:" + left + ",top:" + top + ",right:" + right + ",bottom:" + bottom);
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }

    public interface OnItemClickListener {
        boolean onMenuItemClick(SlideSectionMenuItem item);
    }
}
