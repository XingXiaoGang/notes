package com.mine.view.menu.slide_section_menu;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by gang on 16-4-22.
 * 默认的动画适配器
 */
public class DefaultAnimationAdapter extends MenuAnimationAdapter {

    public Context mContext;
    private int mDrection = 0;
    private static final int DURATION = 500;
    //向上展开
    public static final int DIRECTION_TO_TOP = 0;
    //向下展开
    public static final int DIRECTION_TO_BOTTOM = 1;

    public DefaultAnimationAdapter(Context context) {
        this.mContext = context;
    }

    public DefaultAnimationAdapter(Context context, int direction) {
        this.mContext = context;
        this.mDrection = direction;
    }

    @Override
    Animation getOpenAnimation() {
        int from = 1;
        int to = 0;
        switch (mDrection) {
            case DIRECTION_TO_TOP: {
                from = 1;
                to = 0;
                break;
            }
            case DIRECTION_TO_BOTTOM: {
                from = -1;
                to = 0;
                break;
            }
        }
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 0, TranslateAnimation.RELATIVE_TO_PARENT, from, Animation.RELATIVE_TO_PARENT, to);
        animation.setDuration(DURATION);
        animation.setFillAfter(true);
        animation.setInterpolator(new OvershootInterpolator());
        return animation;
    }

    @Override
    Animation getCloseAnimation() {
        int from = 1;
        int to = 0;
        switch (mDrection) {
            case DIRECTION_TO_TOP: {
                from = -1;
                to = 0;
                break;
            }
            case DIRECTION_TO_BOTTOM: {
                from = 0;
                to = -1;
                break;
            }
        }
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 0, TranslateAnimation.RELATIVE_TO_PARENT, from, Animation.RELATIVE_TO_PARENT, to);
        animation.setDuration(DURATION);
        animation.setFillAfter(true);
        animation.setInterpolator(new OvershootInterpolator());
        return animation;
    }

    @Override
    protected int getType() {
        return TYPE_ITEM;
    }
}
