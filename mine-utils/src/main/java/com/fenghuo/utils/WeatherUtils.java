package com.fenghuo.utils;

import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WeatherUtils {

    private static WeatherUtils mInstance;

    private static final String URL_HOST = "http://apis.baidu.com/heweather/weather/free";
    private static final String API_KEY = "1d6616b12b86f3eef7f7ce739f19f316";

    private WeatherUtils() {
    }

    public static WeatherUtils getInstance() {
        if (mInstance == null) {
            mInstance = new WeatherUtils();
        }
        return mInstance;
    }

    public void getWheather(Handler handler, int what, String place) {
        new getWeatherTask(handler, what, place).start();
    }

    public void getWheather(WeatherCallback callback, String place) {
        new getWeatherTask(callback, place).start();
    }

    //按天的天气
    public class DailyWeatherBean {

        @SerializedName("date")
        @Expose
        public String date;

        @SerializedName("cond")
        @Expose
        public WeatherCond wheatherCond;

        @SerializedName("wind")
        @Expose
        public Wind wind;
    }

    //天气
    public class WeatherCond {
        //天气
        @SerializedName("txt_d")
        @Expose
        public String txt_d;
        //现象
        @SerializedName("txt_n")
        @Expose
        public String txt_n;
    }

    //风
    public class Wind {
        //风向
        @SerializedName("dir")
        @Expose
        public String dir;
        //级数
        @SerializedName("sc")
        @Expose
        public String sc;
    }

    class getWeatherTask extends Thread {

        private WeatherCallback callBack;
        private String place;
        private Handler handler;
        private int messageWhat;

        private int method = -1;

        public getWeatherTask(Handler handler, int messageWhat, String place) {
            this.place = place;
            this.messageWhat = messageWhat;
            this.handler = handler;
            method = 0;
        }

        public getWeatherTask(WeatherCallback callback, String place) {
            this.callBack = callback;
            this.place = place;
            method = 1;
        }

        @Override
        public void run() {
            super.run();

            switch (method) {
                case 0: {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(URL_HOST + "?city=" + place);
                    httpGet.addHeader("apikey", API_KEY);
                    String res = null;
                    try {
                        HttpResponse response = client.execute(httpGet);
                        if (response.getStatusLine().getStatusCode() == 200) {
                            final HttpEntity entity = response.getEntity();
                            res = EntityUtils.toString(entity);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final List<DailyWeatherBean> weatherBean = parseJson(res);
                    handler.sendMessage(handler.obtainMessage(messageWhat, weatherBean));
                    break;
                }
                case 1: {
                    String res = "";
                    try {
                        URL url = new URL(URL_HOST + "?city=" + place);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty("apikey", API_KEY);

                        connection.setDoOutput(true); //允许输出流，即允许上传
                        connection.setUseCaches(false); //不使用缓冲
                        connection.setRequestMethod("GET"); //使用get请求
                        InputStream is = connection.getInputStream();   //获取输入流，此时才真正建立链接
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader bufferReader = new BufferedReader(isr);
                        String inputLine = "";
                        while ((inputLine = bufferReader.readLine()) != null) {
                            res += inputLine + "\n";
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final List<DailyWeatherBean> weatherBean = parseJson(res);
                    callBack.onResult(weatherBean);
                    break;
                }
            }
        }

        private List<DailyWeatherBean> parseJson(String json) {
            if (json == null) {
                return null;
            }
            List<DailyWeatherBean> weatherBeans = new ArrayList<>();
            final Gson gson = new Gson();
            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            try {
                reader.beginObject();
                while (reader.hasNext()) {
                    String tag1 = reader.nextName();
                    if ("HeWeather data service 3.0".equals(tag1)) {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            reader.beginObject();
                            while (reader.hasNext()) {
                                String tag = reader.nextName();
                                if ("daily_forecast".equals(tag)) {
                                    DailyWeatherBean weatherBean;
                                    reader.beginArray();
                                    while (reader.hasNext()) {
                                        weatherBean = gson.fromJson(reader, DailyWeatherBean.class);
                                        weatherBeans.add(weatherBean);
                                    }
                                    reader.endArray();
                                } else {
                                    reader.skipValue();
                                }
                            }
                            reader.endObject();
                        }
                        reader.endArray();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (DailyWeatherBean weatherBean : weatherBeans) {
                System.out.println("parseJson:" + gson.toJson(weatherBean));
            }
            return weatherBeans;
        }
    }

    public abstract class WeatherCallback {
        protected abstract void onResult(List<DailyWeatherBean> list);
    }
}