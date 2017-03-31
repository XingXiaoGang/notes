package com.fenghuo.notes.upload;

import android.os.Handler;
import android.util.Log;

import com.fenghuo.notes.R;
import com.fenghuo.utils.FileUtils;
import com.sina.cloudstorage.auth.AWSCredentials;
import com.sina.cloudstorage.auth.BasicAWSCredentials;
import com.sina.cloudstorage.services.scs.SCS;
import com.sina.cloudstorage.services.scs.SCSClient;
import com.sina.cloudstorage.services.scs.model.PutObjectResult;
import com.sina.cloudstorage.services.scs.model.S3Object;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by gang on 17-2-19.
 */
public class CloudUtils {

    public static final String TAG = "test.cloud";

    private static ScheduledExecutorService mExecutorService;

    static {
        mExecutorService = new ScheduledThreadPoolExecutor(5);
    }

    public static void doUpload(final String buckte, final String key, final File file, final Handler handler) {

        checkKey(key);

        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                String accessKey = Config.AccessKey;
                String secretKey = Config.SecretKey;
                AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
                SCS conn = new SCSClient(credentials);

                //delete old if exist
                conn.deleteObject(buckte, key);

                //put the new object
                PutObjectResult putObjectResault = conn.putObject(buckte, key, file);
                Log.d(TAG, "run: doUpload:" + putObjectResault);
                if (handler != null) {
                    handler.sendEmptyMessage(R.id.file_upload_ok);
                }
            }
        });
    }

    public static void doDownload(final String buckte, final String fileKey, final File destFile, final Handler handler) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                String accessKey = Config.AccessKey;
                String secretKey = Config.SecretKey;
                AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
                SCS conn = new SCSClient(credentials);
                //获取备份文件
                S3Object object = conn.getObject(buckte, fileKey);
                if (object != null) {
                    Log.d(TAG, "run: doDownload：" + object);
                    InputStream inputStream = object.getObjectContent();
                    if (inputStream != null) {
                        boolean res = FileUtils.writeToFile(object.getObjectContent(), destFile);
                        Log.d(TAG, "run: file saved. file=" + object.getKey());
                        if (handler != null) {
                            if (res) {
                                handler.sendEmptyMessage(R.id.file_download_ok);
                            } else {
                                handler.sendEmptyMessage(R.id.file_download_error);
                            }
                        }
                    }
                }
            }
        });
    }

    public static void checkKey(String key) {
        if (key == null || !key.startsWith(Config.dbDir)) {
            throw new RuntimeException("illegal key path");
        }
    }
}
