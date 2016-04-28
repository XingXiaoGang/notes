
package com.fenghuo.utils;

import java.io.UnsupportedEncodingException;

public class Base64Util {

    public static final String CHARSET = "utf-8";

//    public static String encode(byte[] binaryData) {
//        try {
//            return new String(Base64.encodeBase64(binaryData), CHARSET);
//        } catch (UnsupportedEncodingException e) {
//            return null;
//        }
//    }

    /**
     * base64 中含有不能作为文件名的特殊字符串： + 和 / ，该函数生成的base64字串中，+ 替换成 -， / 替换成 _
     * 这样生成的字符串就能作为文件名 该函数对应的解码函数是{@link #decodeFromFileName}
     * <p/>
     * Deprecated, please pse {@link #encodeToSafeCode}
     *
     * @param filename
     * @return
     */
    @Deprecated
    public static String encodeForFileName(String filename) {
        return encodeToSafeCode(filename);
    }

    /**
     * base64 中含有不能作为文件名等的特殊字符串： + 和 / ，该函数生成的base64字串中，+ 替换成 -， / 替换成 _
     * 这样生成的字符串就能作为文件名等 该函数对应的解码函数是{@link #decodeFromFileName}
     *
     * @param data
     * @return
     */
    public static String encodeToSafeCode(String data) {
        String base64 = encodeString(data);
        if (base64 == null) {
            return null;
        }
        return encodeUriSpecialChar(base64);
    }

    /**
     * {@link #encodeToSafeCode} 对应的解码函数
     * <p/>
     * Deprecated, please pse {@link #decodeFromSafeCode}
     *
     * @param filename
     * @return
     */
    public static String decodeFromFileName(String filename) {
        return decodeFromSafeCode(filename);
    }

    /**
     * {@link #encodeToSafeCode} 对应的解码函数
     *
     * @param data
     * @return
     */
    public static String decodeFromSafeCode(String data) {
        return decodeString(decodeSpecialChar(data));
    }

    /**
     * 替换文件名和uri不支持的特殊字符
     *
     * @param oriCode
     * @return
     */
    public static String encodeUriSpecialChar(String oriCode) {
        if (oriCode == null) {
            return null;
        }
        return oriCode.replace("+", "-").replace("/", "_").replace("=", "");
    }

    /**
     * 还原替换了的字符
     *
     * @param oriCode
     * @return
     */
    public static String decodeSpecialChar(String oriCode) {
        if (oriCode == null) {
            return null;
        }
        return oriCode.replace("-", "+").replace("_", "/");
    }

    public static byte[] decode(String base64String) {
        try {
//            return Base64.decodeBase64(base64String.getBytes(CHARSET));
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String encodeString(String plain) {
        return encodeString(plain, CHARSET);
    }

    public static String encodeString(String plain, String encoding) {
        if (plain == null) {
            return null;
        }
        try {
//            return encode(plain.getBytes(encoding));
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String decodeString(String base64) {
        return decodeString(base64, CHARSET);
    }

    public static String decodeString(String base64, String encoding) {
        if (base64 == null) {
            return null;
        }
        try {
            return new String(decode(base64), encoding);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

}
