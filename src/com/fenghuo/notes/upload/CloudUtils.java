package com.fenghuo.notes.upload;

import android.os.Handler;

import com.sina.cloudstorage.auth.AWSCredentials;
import com.sina.cloudstorage.auth.BasicAWSCredentials;
import com.sina.cloudstorage.services.scs.SCS;
import com.sina.cloudstorage.services.scs.SCSClient;
import com.sina.cloudstorage.services.scs.model.Bucket;
import com.sina.cloudstorage.services.scs.model.PutObjectResult;

import java.io.File;
import java.util.List;
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

    public static void doUpload(String fileNameOnServer, final File file, Handler handler) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                String accessKey = Config.AccessKey;
                String secretKey = Config.SecretKey;
                AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
                SCS conn = new SCSClient(credentials);
                PutObjectResult result = conn.putObject(Config.Buckte, "", file);
            }
        });
    }

    public static boolean queryUser(String name, String pwd, Handler handler) {
        return false;
    }

    public static void doDownload() {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                String accessKey = Config.AccessKey;
                String secretKey = Config.SecretKey;
                AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
                SCS conn = new SCSClient(credentials);
                List<Bucket> list = conn.listBuckets();
            }
        });
    }
}
