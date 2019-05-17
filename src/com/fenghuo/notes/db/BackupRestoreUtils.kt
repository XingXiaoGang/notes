package com.fenghuo.notes.db

import android.content.Context
import android.os.Environment
import android.util.Log
import com.fenghuo.notes.bean.Alarm
import com.fenghuo.notes.bean.Kind
import com.fenghuo.notes.bean.MonthAccount
import com.fenghuo.notes.bean.Note
import com.fenghuo.notes.upload.CloudUtils
import com.fenghuo.utils.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class BackupRestoreUtils(private val context: Context) {
    private val DB_SD_PATH = Environment.getExternalStorageDirectory().path + "/Android/backups/" + context.packageName
    private val DB_NAME = "data.db"

    companion object {
        const val SUCCESS = 0
        const val ERROR_FILE_NOT_FOUND = 1
        const val ERROR_UNKNOWN = 2
    }

    /***
     * 当前sdk卡是否有备份
     * */
    fun isExternalDBExist(): Boolean {
        return File(DB_SD_PATH + File.separator + DB_NAME).exists()
    }

    //本地备份
    fun backupLocal(): Boolean {
        val pathFrom = context.getDatabasePath(DB_NAME).absolutePath
        val pathTo = DB_SD_PATH + File.separator + DB_NAME

        Log.i(CloudUtils.TAG, "备份数据库: from=$pathFrom to=$pathTo")

        if (!File(pathFrom).exists()) {
            return false
        }

        return writeFile(pathFrom, pathTo)
    }

    //本地恢复
    fun restoreLocal(): Int {
        val pathTo = context.getDatabasePath(DB_NAME).absolutePath
        val pathFrom = DB_SD_PATH + File.separator + DB_NAME

        Log.i(CloudUtils.TAG, "恢复数据库: from=$pathFrom to=$pathTo")

        if (!File(pathFrom).exists()) {
            return ERROR_FILE_NOT_FOUND
        }
        context.databaseList().forEach {
            context.deleteDatabase(it)
            Log.d(CloudUtils.TAG, "刪除当前数据库:  " + context.getDatabasePath(it))
        }

        return if (writeFile(pathFrom, pathTo)) SUCCESS else ERROR_UNKNOWN
    }

    private fun writeFile(from: String, to: String): Boolean {
        try {
            val toFile = File(to)
            val fromFile = File(from)

            if (!fromFile.exists()) {
                return false
            }
            if (!toFile.exists()) {
                toFile.createNewFile()
            }
            val streamFrom = FileInputStream(from)
            val streamTo = FileOutputStream(to)
            val bs = ByteArray(512)
            var count: Int = streamFrom.read(bs)
            while (count > 0) {
                streamTo.write(bs, 0, count)
                count = streamFrom.read(bs)
            }
            streamFrom.close()
            streamTo.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun readFileEncrypt(from: String, to: String): Boolean {
        try {
            val toFile = File(to)
            val fromFile = File(from)

            if (!fromFile.exists()) {
                return false
            }
            if (!toFile.exists()) {
                toFile.createNewFile()
            }
            val streamFrom = FileInputStream(from)
            val streamTo = FileOutputStream(to)
            val head = ByteArray(128)
            val bs = ByteArray(512)
            //跳过head
            streamFrom.read(head)
            var count = streamFrom.read(bs)
            while (count > 0) {
                streamTo.write(bs, 0, count)
                count = streamFrom.read(bs)
            }
            streamFrom.close()
            streamTo.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun createFileEncrypt(from: String, to: String): Boolean {
        try {
            val fromFile = File(from)
            FileUtils.delete(to)
            val streamFrom = FileInputStream(fromFile)
            val streamTo = FileOutputStream(to, false)
            //add head
            val head = ByteArray(128)
            val random = Random(System.currentTimeMillis())
            random.nextBytes(head)
            streamTo.write(head)
            //add content
            val bs = ByteArray(512)
            var count = streamFrom.read(bs)
            while (count > 0) {
                streamTo.write(bs, 0, count)
                count = streamFrom.read(bs)
            }
            streamFrom.close()
            streamTo.close()
            return true

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    //解密并恢复
    fun restoreEncryptFile(fromFile: String): Int {
        val pathTo = context.getDatabasePath(DB_NAME).absolutePath

        Log.d(CloudUtils.TAG, "恢复数据库: pathTo=$pathTo pathFrom=$fromFile")

        if (!File(fromFile).exists()) {
            return ERROR_FILE_NOT_FOUND
        }
        context.databaseList().forEach {
            context.deleteDatabase(it)
            Log.d(CloudUtils.TAG, "刪除当前数据库:  " + context.getDatabasePath(it))
        }

        return if (readFileEncrypt(fromFile, pathTo)) SUCCESS else ERROR_UNKNOWN
    }

    //防君子不防小人的加密
    fun copyDbFileWithEncrypt(toFileName: String): Boolean {
        val pathto = toFileName
        val pathfrom = context.getDatabasePath(DB_NAME)
        if (!pathfrom.exists()) {
            return false
        }
        val res = createFileEncrypt(pathfrom.absolutePath, pathto)
        Log.d(CloudUtils.TAG, "创建加密数据库: pathTo=$toFileName ")
        return res
    }
}
