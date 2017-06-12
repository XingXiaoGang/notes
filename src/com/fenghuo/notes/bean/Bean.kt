package com.fenghuo.notes.bean

import com.google.gson.Gson

/**
 * Created by gang on 16-4-28.
 */
open class Bean {

    fun toJsonString(): String {
        return gson!!.toJson(this)
    }

    companion object {

        private var gson: Gson? = null

        init {
            gson = Gson()
        }

        fun <T : Bean> readFromJson(json: String, clazz: Class<T>): T {
            return gson!!.fromJson(json, clazz)
        }
    }

}
