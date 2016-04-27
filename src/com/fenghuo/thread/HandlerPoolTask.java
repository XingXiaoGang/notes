package com.fenghuo.thread;

import android.os.Handler;

/**
 * Created by 张少林
 * on 15-3-26 下午5:22
 * Email:zsl@kuba.bz
 * Android Studio
 * Function:
 */
public abstract class HandlerPoolTask extends PoolTask {

    private final Handler mHandler;

    public HandlerPoolTask(Handler handler, String id, boolean removable) {
        super(id, removable);
        mHandler = handler;
    }

    public HandlerPoolTask(Handler handler, String id, int threadPriority, boolean removable) {
        super(id, threadPriority, removable);
        mHandler = handler;
    }

    public HandlerPoolTask(Handler handler, String id) {
        super(id);
        mHandler = handler;
    }

    public HandlerPoolTask(Handler handler, int threadPriority, String id) {
        super(id, threadPriority);
        mHandler = handler;
    }

    public Handler getHandler() {
        return mHandler;
    }

}

