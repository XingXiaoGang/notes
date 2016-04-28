package com.mine.view.menu.slide_section_menu;

import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gang on 16-4-21.
 */
public abstract class MenuAnimationAdapter implements IMenuAnimation {


    private List<Animation> mOpenAnimationsCache = new ArrayList<>();
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_CONTENT = 1;

    @Override
    public final List<Animation> getAnimations(int itemNum) {
        int offset = getStartOffset();
        final List<Animation> list = mOpenAnimationsCache;
        if (list.isEmpty()) {
            switch (getType()) {
                case TYPE_CONTENT: {
                    Animation animation = getAnimation();
                    list.add(animation);
                    break;
                }
                case TYPE_ITEM: {
                    Animation animation;
                    while (itemNum > 0 && ((animation = getAnimation()) != null)) {
                        animation.setStartOffset(offset);
                        offset += offset / 3;
                        list.add(animation);
                        itemNum--;
                    }
                }
                break;
            }
        }
        return list;
    }


    protected int getStartOffset() {
        return 50;
    }

    abstract Animation getAnimation();

    abstract int getType();
}
