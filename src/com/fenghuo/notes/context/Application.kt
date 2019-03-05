package com.fenghuo.notes.context

import io.flutter.view.FlutterMain

/**
 * Created by gang on 16-4-28.
 */
class Application : android.app.Application() {


    override fun onCreate() {
        super.onCreate()
        CrashHandler(this)
        FlutterMain.startInitialization(this)
    }
}
