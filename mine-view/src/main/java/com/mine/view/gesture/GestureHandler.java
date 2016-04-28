package com.mine.view.gesture;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.fenghuo.utils.Size;
import com.fenghuo.utils.Utils;

/**
 * Created by gang on 16-4-28.
 */
public class GestureHandler implements GestureDetector.OnGestureListener {

    private GestureCallBack mGestureCallBack;
    private GestureDetector mGestureDetector;
    private Size mScreenSize;

    public GestureHandler(Context context, GestureCallBack callBack) {
        mGestureDetector = new GestureDetector(context, this);
        this.mGestureCallBack = callBack;
        mScreenSize = Utils.getScreenSize(context);
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
        int left = (int) (mScreenSize.width / 4 * 3);
        int bottom = (int) (mScreenSize.height / 4 * 3);
        if (e1.getRawX() > left && e2.getRawX() > left && e1.getRawY() < bottom && e2.getRawY() < bottom) {
            if (dy > dx) {
                if (mGestureCallBack != null) {
                    if (velocityY > velocityX) {
                        mGestureCallBack.onSlideDown();
                    } else {
                        mGestureCallBack.onSlideUp();
                    }
                }
                return true;
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
