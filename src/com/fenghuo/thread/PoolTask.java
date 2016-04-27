package com.fenghuo.thread;

public abstract class PoolTask implements Runnable {

    public final String id;
    public final boolean removable;
    public final int threadPriority;

    public PoolTask(String id, boolean removable) {
        this(id, android.os.Process.THREAD_PRIORITY_BACKGROUND, removable);
    }

    public PoolTask(String id, int threadPriority, boolean removable) {
        this.id = id;
        this.threadPriority = threadPriority;
        this.removable = removable;
    }

    public PoolTask(String id) {
        this(id, false);
    }

    public PoolTask(String id, int threadPriority) {
        this(id, threadPriority, false);
    }

}
