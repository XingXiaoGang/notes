package com.fenghuo.thread;

import android.os.Process;

public final class ThreadPool implements ITaskQueue {

    // private final static String THREAD_NAME_TEMP = "Thread pool thread#";

    private static ThreadPool sInstance = null;

    private final PoolThread[] mThreads;
    private final ITaskQueue mTaskQueue;
    private final int mMaxSize;
    private final int mMaxWaitTime;
    private final String mThreadNameTemp;

    public synchronized static ThreadPool getInstance() {
        if (sInstance == null) {
            sInstance = new ThreadPool(new LruTaskQueue(), 50, 30000);
        }
        return sInstance;
    }

    private ThreadPool(ITaskQueue taskQueue, int maxSize, int maxWaitTime) {
        mMaxSize = maxSize;
        mMaxWaitTime = maxWaitTime;
        mThreads = new PoolThread[maxSize];
        mTaskQueue = taskQueue;
        mThreadNameTemp = "Pool#" + android.os.Process.myPid() + " Thread#";
    }

    @Override
    public synchronized PoolTask nextTask() {
        return mTaskQueue.nextTask();
    }

    @Override
    public synchronized void addTask(PoolTask task) {
        if (task == null) {
            throw new NullPointerException("task can not be null.");
        }

        if (task.id == null) {
            throw new NullPointerException("task id can not be null.");
        }

        final PoolThread[] threads = mThreads;
        for (PoolThread thread : threads) {
            if (thread == null) {
                continue;
            }
            // 只要正在运行的任务中包含， 就不执行
            if (task.id.equals(thread.getTaskId())) {
                return;
            }
        }

        mTaskQueue.addTask(task);
        notifyNextFreeThread();
    }

    private void notifyNextFreeThread() {

        int firstNullIndex = -1;
        int len = mMaxSize;
        PoolThread[] threads = mThreads;
        PoolThread thread;
        boolean hit = false;

        for (int i = 0; i < len; i++) {
            thread = threads[i];
            if (thread == null) {
                if (firstNullIndex < 0) {
                    firstNullIndex = i;
                }
            } else if (!thread.isBusy) {
                thread.notifySelf();
                hit = true;
                break;
            }
        }

        if (!hit && firstNullIndex >= 0) {
            threads[firstNullIndex] = new PoolThread(firstNullIndex);
            threads[firstNullIndex].start();
        }

    }

    private synchronized void removeThread(int index) {
        mThreads[index] = null;
    }

    private class PoolThread extends Thread {

        public volatile boolean isBusy;
        public boolean isNotified = true;
        public final int id;

        private String taskId;

        PoolThread(int id) {
            super(mThreadNameTemp + id);
            this.id = id;
        }

        @Override
        public void run() {

            while (true) {
                isBusy = true;
                PoolTask task = nextTask();
                if (task != null) {
                    try {
                        Process.setThreadPriority(task.threadPriority);
                    } catch (Exception ex) {
                    }
                    setTaskId(task.id);
                    task.run();
                    setTaskId(null);
                } else {
                    synchronized (this) {
                        if (isNotified) {
                            isNotified = false;
                            isBusy = false;
                            try {
                                wait(mMaxWaitTime);
                            } catch (InterruptedException e) {
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            removeThread(id);
        }

        public synchronized void notifySelf() {
            isNotified = true;
            notify();
        }

        private synchronized void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public synchronized String getTaskId() {
            return taskId;
        }


    }

}
