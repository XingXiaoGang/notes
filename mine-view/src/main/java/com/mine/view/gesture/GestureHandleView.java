package com.mine.view.gesture;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by gang on 16-4-28.
 */
public class GestureHandleView extends View implements GestureDetector.OnGestureListener {

    private GestureCallBack mGestureCallBack;
    private GestureDetector mGestureDetector;

    public GestureHandleView(Context context) {
        super(context);
        initView(context);
    }

    public GestureHandleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public GestureHandleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mGestureDetector = new GestureDetector(context, this);
        setClickable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d("test_gest", "======dispatchTouchEvent=====" + event.getAction());
        return mGestureDetector.onTouchEvent(event);
    }

    public void setGestureCallBack(GestureCallBack callBack) {
        mGestureCallBack = callBack;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return mGestureCallBack != null && mGestureCallBack.onGestureClick();
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (velocityY > velocityX) {
            if (mGestureCallBack != null) {
                if (velocityY > 0) {
                    mGestureCallBack.onSlideDown();
                } else {
                    mGestureCallBack.onSlideUp();
                }
            }

        }
        return false;
    }

    public interface GestureCallBack {
        boolean onGestureClick();

        boolean onSlideDown();

        boolean onSlideUp();
    }
}
