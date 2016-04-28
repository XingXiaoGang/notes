package com.mine.view.gesture;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by gang on 16-4-28.
 */
public class GestureFrameLayout extends FrameLayout {

    private GestureHandler mGestureHandler;

    public GestureFrameLayout(Context context) {
        super(context);
        initView(context);
    }

    public GestureFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public GestureFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        setFocusable(true);
        mGestureHandler = new GestureHandler(context, null);
    }

    public void setGestureCallBack(GestureHandler.GestureCallBack callBack) {
        mGestureHandler.setGestureCallBack(callBack);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //用系统自带的手势处理:弊端就是它会拦截所有事件,导致不能向下分发
        mGestureHandler.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
