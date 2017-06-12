package com.fenghuo.notes.bean

/**
 * 账单的月统计

 * @author Administrator
 */
class MonthAccount : Bean {

    var id: Int = 0
    var things: String? = null
    var date: String? = null
    var sumin: String? = null
    var sumout: String? = null

    constructor(id: Int, things: String, date: String, sumin: String,
                sumout: String) : super() {
        this.id = id
        this.things = things
        this.date = date
        this.sumin = sumin
        this.sumout = sumout
    }

    constructor() : super() {}

    override fun toString(): String {
        return "MonthAccount [id=$id, things=$things, date=" + date + ", sumin=" + sumin + ", sumout=" + sumout + "]"
    }

}
