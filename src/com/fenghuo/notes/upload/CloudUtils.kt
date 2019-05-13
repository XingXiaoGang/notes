package com.fenghuo.notes.upload

import android.os.Handler
import android.util.Log

import com.fenghuo.notes.R
import com.fenghuo.utils.FileUtils
import com.sina.cloudstorage.auth.BasicAWSCredentials
import com.sina.cloudstorage.services.scs.SCSClient

import java.io.File
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor

/**
 * Created by gang on 17-2-19.
 */
object CloudUtils {

    val TAG = "test.cloud"

    private var mExecutorService: ScheduledExecutorService? = null

    init {
        mExecutorService = ScheduledThreadPoolExecutor(5)
    }

    fun doUpload(buckte: String, key: String, file: File, handler: Handler?) {

        checkKey(key)

        mExecutorService!!.submit {
            try {
                val accessKey = Config.AccessKey
                val secretKey = Config.SecretKey
                val credentials = BasicAWSCredentials(accessKey, secretKey)
                val conn = SCSClient(credentials)

                //delete old if exist
                conn.deleteObject(buckte, key)

                //put the new object
                val putObjectResult = conn.putObject(buckte, key, file)
                handler?.sendEmptyMessage(R.id.file_upload_ok)
                Log.i(TAG, "run: doUpload:" + putObjectResult)
            } catch (e: Exception) {
                Log.i(TAG, "run: doUpload error " + e.message)
            }
        }
    }

    fun doDownload(buckte: String, fileKey: String, destFile: File, handler: Handler?) {
        mExecutorService!!.submit {
            val accessKey = Config.AccessKey
            val secretKey = Config.SecretKey
            val credentials = BasicAWSCredentials(accessKey, secretKey)
            val conn = SCSClient(credentials)
            //获取备份文件
            val `object` = conn.getObject(buckte, fileKey)
            if (`object` != null) {
                Log.d(TAG, "run: doDownload：" + `object`)
                val inputStream = `object`.objectContent
                if (inputStream != null) {
                    val res = FileUtils.writeToFile(`object`.objectContent, destFile)
                    Log.d(TAG, "run: file saved. file=" + `object`.key)
                    if (handler != null) {
                        if (res) {
                            handler.sendEmptyMessage(R.id.file_download_ok)
                        } else {
                            handler.sendEmptyMessage(R.id.file_download_error)
                        }
                    }
                }
            }
        }
    }

    fun checkKey(key: String?) {
        if (key == null || !key.startsWith(Config.dbDir)) {
            throw RuntimeException("illegal key path")
        }
    }
}
