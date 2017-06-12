package com.fenghuo.notes.bean

class Kind : Bean {
    var id: Int = 0
    var kind: String? = null

    constructor(id: Int, kind: String) : super() {
        this.id = id
        this.kind = kind
    }

    constructor() : super() {}

}
