package com.fenghuo.notes.context

/**
 * Created by gang on 16-7-10.
 */
class NoteEvent(var what: Int) {
    var argInt1: Int = 0
    var argInt2: Int = 0
    var argBoolean: Boolean = false
    var `object`: Any? = null

    companion object {

        val TPYE_MENU_CLICK = 0
        val TPYE_UPDATE_MENU_STATE = 1
    }
}