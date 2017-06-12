package com.fenghuo.notes.bean

/**
 * 数据库账单 model

 * @author Administrator
 */
class Account : Bean {
    var id: Int = 0
    var kind: Int = 0
    var kinds: String? = null
    var money: Float = 0.toFloat()
    var date: String? = null

    constructor(id: Int, kind: Int, kinds: String, money: Float, date: String) : super() {
        this.id = id
        this.kind = kind
        this.kinds = kinds
        this.money = money
        this.date = date
    }

    constructor() : super() {}

    override fun toString(): String {
        return "Account [id=$id, kind=$kind, kinds=$kinds" + ", money=" + money + ", date=" + date + "]"
    }

}
