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
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class BackupRestoreUtils(private val context: Context) {
    private var alarmHelper: DBAlarmHelper? = null
    private var accountHelper: DBAccountHelper? = null
    private var kindHelper: DbKindHelper? = null
    private var noteHelper: DBNoteHelper? = null

    private var listalarm: List<Alarm>? = null
    private var listKind: List<Kind>? = null
    private var listnote: List<Note>? = null
    private var listaccount: List<MonthAccount>? = null
    // 数据库在手机内存中的目录
    private val DB_phone_path = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/com.fenghuo.notes/databases"
    // 外部存储的备份目录
    private val DB_sd_path = Environment.getExternalStorageDirectory()
            .path + "/Android/backups/com.fenghuo.notes"
    private val DB_name = "data.db"

    // 第一种思路 将所有数据转换成json字符串 存储到内存中

    fun backup(): Boolean {
        alarmHelper = DBAlarmHelper(context)
        kindHelper = DbKindHelper(context)
        noteHelper = DBNoteHelper(context)
        accountHelper = DBAccountHelper(context)
        listalarm = alarmHelper!!.Getall()
        listKind = kindHelper!!.Getlist()
        listnote = noteHelper!!.Getlist()
        listaccount = accountHelper!!.GetListAll()
        // 转换json
        var Jsonalarms = ""
        if (listalarm != null && listalarm!!.size > 0) {
            var map: HashMap<String, String>
            for (alarm in listalarm!!) {
                map = HashMap<String, String>()
                map.put("id", alarm.id.toString() + "")
                map.put("date", alarm.id.toString() + "")
                map.put("time", alarm.id.toString() + "")
                map.put("repeat", alarm.id.toString() + "")
                map.put("week", alarm.id.toString() + "")
                map.put("vibration", alarm.id.toString() + "")
                map.put("ringuri", alarm.id.toString() + "")
                map.put("content", alarm.id.toString() + "")
                val `object` = JSONObject(map)
                Jsonalarms = Jsonalarms + `object`.toString() + "\n"
            }
        }

        return false
    }

    fun restore(): Boolean {

        return false
    }

    fun checkexist(): Boolean {

        val dri = File(DB_sd_path)
        if (!dri.exists()) {
            dri.mkdirs()
            return false
        }
        val file = File(DB_sd_path + "/" + DB_name)
        if (file.exists()) {
            // 如果存在 ，读取数据信息
            // SQLiteDatabase.CursorFactory cursorFactory = new
            // SQLiteDatabase.CursorFactory() {
            // @Override
            // public Cursor newCursor(SQLiteDatabase db,
            // SQLiteCursorDriver masterQuery, String editTable,
            // SQLiteQuery query) {
            // return null;
            // }
            // };
            // SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_sd_path,
            // cursorFactory, 0);

            return true
        }
        return false
    }

    // 第二种思路 直接将数据库导出来 -1 失败 0 未找到数据 1 成功

    fun restoreLocal(): Int {
        val pathto = DB_phone_path + "/" + DB_name
        val pathfrom = DB_sd_path + "/" + DB_name

        Log.d(CloudUtils.TAG, "恢复数据库: pathTo=$pathto\npathFrom=$pathfrom")

        try {
            val to = File(pathto)
            val from = File(pathfrom)
            // 先决断sd卡文件夹是否存在
            val pderectroy = File(DB_sd_path)
            if (!pderectroy.exists()) {
                pderectroy.mkdirs()
                return 0
            }
            // sd卡数据库是否存在
            if (!from.exists()) {
                return 0
            }
            val streami = FileInputStream(from)
            val streamo = FileOutputStream(to)
            val bs = ByteArray(512)
            var count: Int = streami.read(bs)
            while (count > 0) {
                streamo.write(bs, 0, count)
                count = streami.read(bs)
            }
            streami.close()
            streamo.close()
            println("恢复成功===========")
            return 1
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1
    }

    fun backupLocal(): Boolean {
        val pathto = DB_phone_path + "/" + DB_name
        val pathfrom = DB_sd_path + "/" + DB_name
        try {
            val from = File(pathfrom)
            val to = File(pathto)
            val streami = FileInputStream(to)
            val streamo = FileOutputStream(from, false)
            val bs = ByteArray(512)
            var count = streami.read(bs)
            while (count > 0) {
                streamo.write(bs, 0, count)
                count = streami.read(bs)
            }
            streami.close()
            streamo.close()
            println("备份成功！------------")
            return true

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    //解密并恢复
    fun restoreEncyptFile(fileName: String): Int {
        val pathto = DB_phone_path + "/" + DB_name

        Log.d(CloudUtils.TAG, "恢复数据库: pathTo=$pathto\npathFrom=$fileName")

        try {
            val to = File(pathto)
            val from = File(fileName)
            // 先决断sd卡文件夹是否存在
            val pderectroy = File(DB_sd_path)
            if (!pderectroy.exists()) {
                pderectroy.mkdirs()
                return 0
            }
            // sd卡数据库是否存在
            if (!from.exists()) {
                return 0
            }
            val streami = FileInputStream(from)
            val streamo = FileOutputStream(to)
            val head = ByteArray(128)
            val bs = ByteArray(512)
            //跳过head
            streami.read(head)
            var count = streami.read(bs)
            while (count > 0) {
                streamo.write(bs, 0, count)
                count = streami.read(bs)
            }
            streami.close()
            streamo.close()
            println("恢复成功===========")
            return 1
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1
    }

    //防君子不防小人的加密
    fun copyDbFileWithEncypt(toFileName: String): Boolean {
        val pathto = toFileName
        val pathfrom = DB_phone_path + "/" + DB_name
        try {
            val from = File(pathfrom)
            val to = File(pathto)
            FileUtils.delete(to)
            val streami = FileInputStream(from)
            val streamo = FileOutputStream(to, false)
            //add head
            val head = ByteArray(128)
            val random = Random(System.currentTimeMillis())
            random.nextBytes(head)
            streamo.write(head)
            //add content
            val bs = ByteArray(512)
            var count = streami.read(bs)
            while (count > 0) {
                streamo.write(bs, 0, count)
                count = streami.read(bs)
            }
            streami.close()
            streamo.close()
            println("备份成功！------------")
            return true

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
}
