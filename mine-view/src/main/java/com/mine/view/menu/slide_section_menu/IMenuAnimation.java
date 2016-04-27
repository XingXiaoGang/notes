package com.mine.view.menu.slide_section_menu;

import android.view.animation.Animation;

import java.util.List;

/**
 * Created by gang on 16-4-21.
 */
public interface IMenuAnimation {

    List<Animation> getOpenAnimations(int itemNum);

    List<Animation> getCoseAnimations(int itemNum);
}
