package com.mine.view.gesture;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by gang on 16-4-28.
 */
public class GestureHandler implements GestureDetector.OnGestureListener {

    private GestureCallBack mGestureCallBack;
    private GestureDetector mGestureDetector;

    public GestureHandler(Context context, GestureCallBack callBack) {
        mGestureDetector = new GestureDetector(context, this);
        this.mGestureCallBack = callBack;
    }

    public boolean onTouchEvent(MotionEvent event) {
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
        int dx = (int) Math.abs(e2.getX() - e1.getX());
        int dy = (int) Math.abs(e2.getY() - e1.getY());
        if (dy > dx && velocityY > velocityX) {
            if (mGestureCallBack != null) {
                if (velocityY > 0) {
                    mGestureCallBack.onSlideDown();
                } else {
                    mGestureCallBack.onSlideUp();
                }
            }
            return true;
        }
        return false;
    }

    public interface GestureCallBack {
        boolean onGestureClick();

        boolean onSlideDown();

        boolean onSlideUp();
    }
}
