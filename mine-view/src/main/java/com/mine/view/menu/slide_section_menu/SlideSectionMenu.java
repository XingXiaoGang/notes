package com.mine.view.menu.slide_section_menu;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by gang on 16-4-21.
 */
public class SlideSectionMenu extends LinearLayout implements Animation.AnimationListener {

    private static final String TAG = "test_slide";
    //当前的状态
    private State mMenuState = State.OPENED;
    private MenuAnimationAdapter mAnimationAdapter;


    public enum State {
        OPENED, OPENING, CLOSING, CLOSED
    }

    public SlideSectionMenu(Context context) {
        super(context);
        initView(context, null);
    }

    public SlideSectionMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SlideSectionMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setWillNotDraw(true);
        setOrientation(VERTICAL);
        mAnimationAdapter = new DefaultAnimationAdapter(context, DefaultAnimationAdapter.DIRECTION_TO_BOTTOM);
    }

    public void openMenu(boolean anim) {
        if (mMenuState == State.CLOSED) {
            mMenuState = State.OPENING;
            setVisibility(VISIBLE);
            //open
            if (mAnimationAdapter != null && anim) {
                final int childCount = getChildCount();
                List<Animation> animations = mAnimationAdapter.getAnimations(childCount);
                switch (mAnimationAdapter.getType()) {
                    case MenuAnimationAdapter.TYPE_ITEM: {
                        for (int i = 0; i < childCount; i++) {
                            final View child = getChildAt(i);
                            final Animation animation = animations.get(i);
                            if (child.getVisibility() == VISIBLE) {
                                child.startAnimation(animation);
                            }
                            if (i == childCount - 1) {
                                animation.setAnimationListener(this);
                            }
                        }
                        break;
                    }
                    case MenuAnimationAdapter.TYPE_CONTENT: {
                        final Animation animation = animations.get(0);
                        startAnimation(animation);
                        animation.setAnimationListener(this);
                        break;
                    }
                }
            }
        }
    }

    public void closeMenu() {
        if (mMenuState == State.OPENED) {
            mMenuState = State.CLOSED;
            setVisibility(INVISIBLE);
        }
    }

    public void setAnimationAdapter(MenuAnimationAdapter adapter) {
        this.mAnimationAdapter = adapter;
    }

    public State getMenuState() {
        return mMenuState;
    }

    public void toggleMenu(boolean anim) {
        switch (mMenuState) {
            case OPENED: {
                closeMenu();
                break;
            }
            case CLOSED: {
                openMenu(anim);
                break;
            }
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        switch (mMenuState) {
            case OPENING: {
                mMenuState = State.OPENED;
                break;
            }
            case CLOSING: {
                mMenuState = State.CLOSED;
                break;
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
