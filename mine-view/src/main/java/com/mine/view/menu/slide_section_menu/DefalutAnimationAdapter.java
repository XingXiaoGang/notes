package com.mine.view.menu.slide_section_menu;

import android.content.Context;
import android.view.animation.Animation;

/**
 * Created by gang on 16-4-22.
 */
public class DefalutAnimationAdapter extends MenuAnimationAdapter {

    public Context mContext;

    @Override
    protected Animation getOpenAnimation() {
        return null;
    }

    @Override
    protected Animation getCloseAnimation() {
        return null;
    }

    @Override
    protected int getType() {
        return TYPE_ITEM;
    }
}
