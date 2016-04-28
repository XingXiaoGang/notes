package com.fenghuo.notes.context;

/**
 * Created by gang on 16-4-28.
 */
public class Application extends android.app.Application {


    @Override
    public void onCreate() {
        super.onCreate();
        new CrashHandler(this);
    }
}
