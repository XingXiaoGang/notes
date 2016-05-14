package com.fenghuo.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by gang on 16-5-13.
 */
public class HttpUtils {

    //解决 android 6.0不能用： compile 'org.apache.httpcomponents:httpclient:4.5'

    public static String loadGetString(String url) {
        HttpGet httpGet = new HttpGet(url);
        String res = null;
        try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);// 设置请求超时10秒
            HttpConnectionParams.setSoTimeout(httpParameters, 5 * 1000); // 设置等待数据超时10秒
            HttpClient httpClient = new DefaultHttpClient(httpParameters); // 此时构造DefaultHttpClient时将参数传入

            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                res = EntityUtils.toString(response.getEntity());
            } else {
                res = "error code:" + response.getStatusLine().getStatusCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static InputStream loadGetStream(String url) {
        HttpGet httpPost = new HttpGet(url);
        InputStream inputStream = null;
        try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);// 设置请求超时10秒
            HttpConnectionParams.setSoTimeout(httpParameters, 5 * 1000); // 设置等待数据超时10秒
            HttpClient httpClient = new DefaultHttpClient(httpParameters); // 此时构造DefaultHttpClient时将参数传入

            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                inputStream = response.getEntity().getContent();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public static String loadPostString(String url, List<NameValuePair> parameters) {
        HttpPost httpPost = new HttpPost(url);
        String res = null;
        try {
            HttpEntity entity = new UrlEncodedFormEntity(parameters);
            httpPost.setEntity(entity);

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);// 设置请求超时10秒
            HttpConnectionParams.setSoTimeout(httpParameters, 5 * 1000); // 设置等待数据超时10秒
            HttpClient httpClient = new DefaultHttpClient(httpParameters); // 此时构造DefaultHttpClient时将参数传入

            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                res = EntityUtils.toString(response.getEntity());
            } else {
                res = "error code:" + response.getStatusLine().getStatusCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static InputStream loadPostStream(String url, List<NameValuePair> parameters) {
        HttpPost httpPost = new HttpPost(url);
        InputStream inputStream = null;
        try {
            HttpEntity entity = new UrlEncodedFormEntity(parameters);
            httpPost.setEntity(entity);

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);// 设置请求超时10秒
            HttpConnectionParams.setSoTimeout(httpParameters, 5 * 1000); // 设置等待数据超时10秒
            HttpClient httpClient = new DefaultHttpClient(httpParameters); // 此时构造DefaultHttpClient时将参数传入

            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                inputStream = response.getEntity().getContent();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

}
