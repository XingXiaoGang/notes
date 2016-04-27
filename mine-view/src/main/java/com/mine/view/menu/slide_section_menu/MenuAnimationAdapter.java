package com.mine.view.menu.slide_section_menu;

import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gang on 16-4-21.
 */
public abstract class MenuAnimationAdapter implements IMenuAnimation {


    private List<Animation> mOpenAnimationsCache = new ArrayList<>();
    private List<Animation> mCloseAnimationsCache = new ArrayList<>();

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_CONTENT = 1;

    @Override
    public final List<Animation> getOpenAnimations(int itemNum) {

        final int offset = getStartOffset();
        final List<Animation> list = mOpenAnimationsCache;
        if (list.isEmpty()) {
            switch (getType()) {
                case TYPE_CONTENT: {
                    Animation animation = getOpenAnimation();
                    list.add(animation);
                    break;
                }
                case TYPE_ITEM: {
                    Animation animation;
                    while (itemNum > 0 && ((animation = getOpenAnimation()) != null)) {
                        animation.setStartOffset(offset);
                        list.add(animation);
                        itemNum--;
                    }
                }
                break;
            }
        }
        return list;
    }

    @Override
    public List<Animation> getCoseAnimations(int itemNum) {

        final int offset = getStartOffset();
        final List<Animation> list = mCloseAnimationsCache;
        if (list.isEmpty()) {
            switch (getType()) {
                case TYPE_CONTENT: {
                    Animation animation = getOpenAnimation();
                    list.add(animation);
                    break;
                }
                case TYPE_ITEM: {
                    Animation animation;
                    while (itemNum > 0 && ((animation = getCloseAnimation()) != null)) {
                        animation.setStartOffset(offset);
                        list.add(animation);
                        itemNum--;
                    }
                    break;
                }
            }

        }
        return list;
    }

    protected int getStartOffset() {
        return 50;
    }

    abstract Animation getOpenAnimation();

    abstract Animation getCloseAnimation();

    abstract int getType();
}
