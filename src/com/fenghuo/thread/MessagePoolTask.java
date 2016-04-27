package com.fenghuo.thread;


import com.fenghuo.handler.ISender;

/**
 * Created with Android Studio
 * <p/>
 * Project: themelauncher
 * Author: zhangshaolin
 * Date:   14/12/22
 * Time:   下午11:17
 * Email:  zsl@kuba.bz
 */
public abstract class MessagePoolTask extends PoolTask {

    private final ISender mSender;

    public MessagePoolTask(ISender sender, String id, boolean removable) {
        super(id, removable);
        mSender = sender.getReference();
    }

    public MessagePoolTask(ISender sender, String id, int threadPriority, boolean removable) {
        super(id, threadPriority, removable);
        mSender = sender.getReference();
    }

    public MessagePoolTask(ISender sender, String id) {
        super(id);
        mSender = sender.getReference();
    }

    public MessagePoolTask(ISender sender, int threadPriority, String id) {
        super(id, threadPriority);
        mSender = sender.getReference();
    }

    public ISender getSender() {
        return mSender;
    }

}
