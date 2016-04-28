package com.mine.view.menu.slide_section_menu;

import android.view.animation.Animation;

import java.util.List;

/**
 * Created by gang on 16-4-21.
 */
public interface IMenuAnimation {

    List<Animation> getAnimations(int itemNum);
// 关闭的时候一般不用动画 用动画感觉反而体验不好
//    List<Animation> getCoseAnimations(int itemNum);
}
