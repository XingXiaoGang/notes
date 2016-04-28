package com.fenghuo.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by gang on 16-4-28.
 */
public class Utils {

    private static Size mCachedSize;

    public static Size getScreenSize(Context context) {
        if (mCachedSize == null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Size size = new Size();
            if (wm != null) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(displayMetrics);
                size.set(displayMetrics.widthPixels, displayMetrics.heightPixels);
            }
            mCachedSize = size;
        }
        return mCachedSize;
    }

    public static float px2dp(float pxValue, Context context) {
        DisplayMetrics dm = context.getResources()
                .getDisplayMetrics();
        return pxValue / dm.density;
    }

    public static float dp2px(float dpValue, Context context) {
        DisplayMetrics dm = context.getResources()
                .getDisplayMetrics();
        return dpValue * dm.density;
    }
}
