
package com.fenghuo.thread;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class LruTaskQueue implements ITaskQueue {

    private static final int MAX_ENTRIES = 200;

    private LinkedHashMap<String, PoolTask> mQueue = new LinkedHashMap<String, PoolTask>(
            MAX_ENTRIES, .75F, true) {

        private static final long serialVersionUID = 5891565084361055949L;

        protected boolean removeEldestEntry(Map.Entry<String, PoolTask> eldest) {

            if (eldest.getValue() == null) {
                return true;
            }

            return size() > MAX_ENTRIES;
        }
    };

    private HashSet<String> mUnRemovableQueueKeys = new HashSet<String>();
    private LinkedList<PoolTask> mUnRemovableQueue = new LinkedList<PoolTask>();

    @Override
    public PoolTask nextTask() {

        if (!mUnRemovableQueue.isEmpty()) {
            PoolTask task = mUnRemovableQueue.removeFirst();
            if (task != null) {
                mUnRemovableQueueKeys.remove(task.id);
                return task;
            }
        }

        String id = null;

        try {
            id = mQueue.keySet().iterator().next();
        } catch (Exception ex) {
        }

        if (id != null) {
            return mQueue.remove(id);
        }
        return null;
    }

    @Override
    public void addTask(PoolTask task) {

        if (task.removable) {
            PoolTask old = mQueue.get(task.id);
            if (old == null) {
                mQueue.put(task.id, task);
            }
        } else {
            if (!mUnRemovableQueueKeys.contains(task.id)) {
                mUnRemovableQueue.add(task);
                mUnRemovableQueueKeys.add(task.id);
            }
        }
    }
}
