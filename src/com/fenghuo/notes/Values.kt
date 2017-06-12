package com.fenghuo.notes

object Values {

    val TAG = "test.main"


    val DBName = "data.db"
    val TableNote = "notes"
    val TableAccount = "accounts"
    val TableKind = "kinds"
    val TableAlarm = "alarms"
    val backupPath = ""
    //
    //标志是否为恢复数据库
    var isRestore_database = false
    //
    var item_img = R.id.item_img
    var item_tv_content = R.id.item_tv_content
    //
    var item_bg_preview = intArrayOf(R.drawable.page_yellow, R.drawable.page_white, R.drawable.page_pink, R.drawable.page_green, R.drawable.page_blue)

    var item_bg_big = intArrayOf(R.drawable.page_bg_yellow, R.drawable.page_bg_white, R.drawable.page_bg_pink, R.drawable.page_bg_green, R.drawable.page_bg_blue)

    val CODE_SET_LOCK = 123
    val CODE_UN_LOCK = 234
}
