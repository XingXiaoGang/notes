
package com.fenghuo.cache;

import com.fenghuo.handler.ISender;
import com.fenghuo.thread.ThreadPool;
import com.fenghuo.utils.Base64Util;
import com.fenghuo.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class FileCache<V> extends AbsAsyncCache<String, V> {

    private File mCacheDir;
    private String mFileUri;
    private ThreadPool mThreadPool;

    public FileCache() {
        mThreadPool = ThreadPool.getInstance();
    }

    @Override
    public void put(String key, V value) {
        File cacheFile = new File(getCacheDir(), keyToFileName(key));
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(cacheFile);
            writeValue(out, value);
        } catch (Exception e) {
        } finally {
            FileUtils.sync(out);
            FileUtils.close(out);
        }
    }

    @Override
    public void remove(String key) {
        File cacheFile = getValueFile(key);
        if (FileUtils.exists(cacheFile)) {
            cacheFile.delete();
        }
    }

    public final File getValueFile(String key) {
        return new File(getCacheDir(), keyToFileName(key));
    }

    @Override
    protected void getSync(String key) {
        if (hasValue(key)) {
            mThreadPool.addTask(newReadTask(key, getTaskId(mFileUri, key)));
        }
    }

    protected boolean hasValue(String key) {
        File cacheFile = getValueFile(key);
        return FileUtils.exists(cacheFile);
    }

    protected abstract AbsFileTask<V> newReadTask(String key, String taskId);

    @Override
    public void clear() {

        File[] files = getCacheDir().listFiles();

        if (files == null || files.length <= 0) {
            return;
        }

        for (File file : getCacheDir().listFiles()) {
            FileUtils.delete(file);
        }
    }

    abstract protected void writeValue(OutputStream out, V value);

    public synchronized File getCacheDir() {
        if (mCacheDir == null) {
            mCacheDir = newCacheDir();
            if (!FileUtils.isDirectory(mCacheDir)) {
                FileUtils.delete(mCacheDir);
            }
            mCacheDir.mkdirs();
            mFileUri = "file://" + mCacheDir.getAbsolutePath() + File.separator;
        }
        return mCacheDir;
    }

    abstract protected File newCacheDir();

    public static String keyToFileName(String key) {
        return Base64Util.encodeForFileName(key);
    }

    public static String fileNameToKey(String filename) {
        return Base64Util.decodeFromFileName(filename);
    }

    private static String getTaskId(String fileUri, String key) {
        if (fileUri.endsWith(File.separator)) {
            return fileUri + keyToFileName(key);
        }
        return fileUri + File.separator + keyToFileName(key);
    }

    protected abstract static class AbsFileTask<V> extends AbsAsyncTask<String, V> {

        private final File cacheDir;

        public AbsFileTask(File cacheDir, ISender sender, String key, String id, boolean removable) {
            super(sender, key, id, removable);
            this.cacheDir = cacheDir;
        }

        @Override
        protected V get(String key) {
            File cacheFile = getCacheFile(key);
            FileInputStream in = null;
            V value = null;
            try {
                in = new FileInputStream(cacheFile);
                value = readValue(in);
            } catch (Exception e) {
            } finally {
                FileUtils.close(in);
            }
            return value;
        }

        protected abstract V readValue(InputStream in);

        public File getCacheFile(String key) {
            return new File(cacheDir, keyToFileName(key));
        }

        public File getCacheDir() {
            return cacheDir;
        }

    }

    @Override
    public int size() {
        return getCacheDir().list().length;
    }

}
