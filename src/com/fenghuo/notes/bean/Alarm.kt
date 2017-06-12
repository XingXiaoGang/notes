package com.fenghuo.notes.bean

class Alarm : Bean {
    var id: Int = 0
    var date: String? = null
    var time: String? = null
    var repeat: Int = 0
    var week: String? = null
    var vibration: Int = 0
    var ringUri: String? = null
    var content: String? = null

    constructor(id: Int, date: String, time: String, repeat: Int, week: String,
                vibration: Int, ringUri: String, content: String) : super() {
        this.id = id
        this.date = date
        this.time = time
        this.repeat = repeat
        this.week = week
        this.vibration = vibration
        this.ringUri = ringUri
        this.content = content
    }

    constructor() : super() {}

    override fun toString(): String {
        return "Alarm [id=$id, date=$date, time=$time" + ", repeat=" + repeat + ", week=" + week + ", vibration="
        (+vibration).toString() + ", ringUri=" + ringUri + ", content=" + content + "]"
    }


}
