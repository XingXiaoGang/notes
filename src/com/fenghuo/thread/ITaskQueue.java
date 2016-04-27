
package com.fenghuo.thread;

public interface ITaskQueue {

    PoolTask nextTask();

    void addTask(PoolTask task);

}
