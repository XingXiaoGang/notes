package com.fenghuo.notes.bean

class Note : Bean {
    var id: Int = 0
    var img: Int = 0
    var content: String? = null
    var date: String? = null
    var alarm: Int = 0

    constructor(id: Int, img: Int, content: String, date: String, alarm: Int) : super() {
        this.id = id
        this.img = img
        this.content = content
        this.date = date
        this.alarm = alarm
    }

    constructor() : super() {}

    override fun toString(): String {
        return "Note [id=$id, img=$img, content=$content" + ", date=" + date + ", alarm=" + alarm + "]"
    }

}
