package com.fenghuo.notes.bean;

import com.google.gson.Gson;

/**
 * Created by gang on 16-4-28.
 */
public class Bean {

    private static Gson gson;

    static {
        gson = new Gson();
    }

    public final String toJsonString() {
        return gson.toJson(this);
    }

    public static <T extends Bean> T readFromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

}
