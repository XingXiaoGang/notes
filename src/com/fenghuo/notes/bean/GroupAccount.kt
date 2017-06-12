package com.fenghuo.notes.bean

/**
 * 分组数据model

 * @author Administrator
 */
class GroupAccount : Bean {
    var id: Int = 0
    var name: String? = null
    var sumin: String? = null
    var sumout: String? = null

    constructor(id: Int, name: String, sumin: String, sumout: String) : super() {
        this.id = id
        this.name = name
        this.sumin = sumin
        this.sumout = sumout
    }

    constructor() : super() {}

    override fun toString(): String {
        return "GroupAccount [id=$id, name=$name, sumin=$sumin" + ", sumout=" + sumout + "]"
    }

}
