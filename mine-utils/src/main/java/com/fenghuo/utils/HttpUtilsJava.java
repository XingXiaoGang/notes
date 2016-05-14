package com.fenghuo.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by gang on 16-5-14.
 * java 标准网络请求方式
 */
public class HttpUtilsJava {

    public static String loadGetString(String path) {
        InputStream inputStream = loadGetStream(path);
        String res = null;
        if (inputStream != null) {
            try {
                res = FileUtils.readFileToString(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static InputStream loadGetStream(String path) {
        InputStream stream = null;
        HttpURLConnection connection;
        try {
            final URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接网络的超时时间
            connection.setConnectTimeout(5000);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = connection.getInputStream();
            } else {
                stream = connection.getErrorStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }

    public static String loadPostString(String path, List<NameValuePair> parameter) {
        InputStream inputStream = loadPostStream(path, parameter);
        String res = null;
        if (inputStream != null) {
            try {
                res = FileUtils.readFileToString(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static InputStream loadPostStream(String path, List<NameValuePair> parameter) {
        InputStream stream = null;
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(path).openConnection();
            connection.setConnectTimeout(5000);
            connection.setDoOutput(true);// 表示向服务器写数据 , setDoInput默认为true
            // 表示设置请求体的类型是文本类型
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (parameter != null) {
                String para = URLEncodedUtils.format(parameter, "utf-8");
                byte[] data = para.getBytes();
                connection.setRequestProperty("Content-Length", String.valueOf(data.length));
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data);
            }
            // 输出完成 接下来获取服务器响应
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = connection.getInputStream();
            } else {
                stream = connection.getErrorStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }

    public abstract class ProgressCallback {
        abstract void onProgressChanged(int full, int current);
    }
}
