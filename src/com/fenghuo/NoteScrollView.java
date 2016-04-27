package com.fenghuo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

/**
 * Created by gang on 16-2-3.
 */
public class NoteScrollView extends ScrollView {

    public NoteScrollView(Context context) {
        super(context);
    }

    public NoteScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoteScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //基类先测量
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //第二次测量
        if (getChildCount() > 0) {
            final View child = getChildAt(0);
            int height = getMeasuredHeight();
            final int childHeight = child.getMeasuredHeight();
            if (childHeight < height) {
                final FrameLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), lp.width);
                height -= getPaddingTop();
                height -= getPaddingBottom();
                int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }
}
