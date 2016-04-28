package com.fenghuo.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * Created with Android Studio
 * <p/>
 * Project: lockscreen
 * Author: zhangshaolin(www.iooly.com)
 * Date:   14-6-16
 * Time:   下午7:22
 * Email:  app@iooly.com
 */
public class CommonUtils {
    public static int hexStringToColor(CharSequence hexStr) {
        if (TextUtils.isEmpty(hexStr)) {
            throw new IllegalArgumentException("Hex string is empty.");
        }

        if (hexStr.length() != 9) {
            throw new IllegalArgumentException("String length wrong.");
        }

        if (hexStr.charAt(0) != '#') {
            throw new IllegalArgumentException(
                    "Hex string must starts with '#'");
        }

        int color = getOtcValueFromHexChar(hexStr.charAt(1));

        for (int i = 1; i < 8; i++) {
            color <<= 4;
            color |= getOtcValueFromHexChar(hexStr.charAt(i + 1));
        }

        return color;
    }

    public static int getOtcValueFromHexChar(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }

        if (c >= 'a' && c <= 'f') {
            return 10 + c - 'a';
        }

        if (c >= 'A' && c <= 'F') {
            return 10 + c - 'A';
        }

        throw new IllegalArgumentException(c + "not a hex char.");
    }

    public static void copyToClipboard(Context context, CharSequence text, CharSequence label) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText(label, text);
            cmb.setPrimaryClip(data);
        } else {
            android.text.ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(text);
        }
    }

    public static boolean isFlyme() {
        boolean result = false;
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            result = method != null;
        } catch (Exception e) {
        }
        return result;
    }

}
