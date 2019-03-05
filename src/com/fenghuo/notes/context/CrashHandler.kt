package com.fenghuo.notes.context

import android.content.Context
import android.os.Build
import android.os.SystemClock
import com.fenghuo.notes.BuildConfig
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by gang on 16-4-28.
 */
class CrashHandler internal constructor(internal var context: Context) : Thread.UncaughtExceptionHandler {

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        ex.printStackTrace()
        val dir = context.getExternalFilesDir("logs")
        val file = File(dir, "crash.log")
        try {
            file.createNewFile()
            val writer = FileWriter(file)
            //记录手机信息
            writeInformation(writer)
            //记录log信息
            writer.write("\n\n")
            writeCrash(ex, writer)
            writer.flush()
            writer.close()
            throw ex;
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            ex.printStackTrace()
            SystemClock.sleep(200)
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)
        }
    }

    //log信息
    private fun writeCrash(ex: Throwable, writer: FileWriter) {
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause: Throwable? = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
    }

    //手机信息
    @Throws(IOException::class)
    private fun writeInformation(fw: FileWriter) {
        fw.write("pkg_name=" + BuildConfig.APPLICATION_ID)
        fw.write("\nversionCode=" + BuildConfig.VERSION_CODE)
        fw.write("\nversionName=" + BuildConfig.VERSION_NAME)
        fw.write("\ntimestamp=" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()))
        fw.write("\nboard=" + Build.BOARD)
        fw.write("\nbootloader=" + Build.BOOTLOADER)
        fw.write("\nbrand=" + Build.BRAND)
        fw.write("\ncpu_abi=" + Build.CPU_ABI)
        fw.write("\ncpu_abi2=" + Build.CPU_ABI2)
        fw.write("\ndevice=" + Build.DEVICE)
        fw.write("\ndisplay=" + Build.DISPLAY)
        fw.write("\nfingerprint=" + Build.FINGERPRINT)
        fw.write("\nhardware=" + Build.HARDWARE)
        fw.write("\nhost=" + Build.HOST)
        fw.write("\nid=" + Build.ID)
        fw.write("\nmanufacturer=" + Build.MANUFACTURER)
        fw.write("\nmodel=" + Build.MODEL)
        fw.write("\nproduct=" + Build.PRODUCT)
        fw.write("\nradio=" + Build.RADIO)
        fw.write("\ntags=" + Build.TAGS)
        fw.write("\nuser=" + Build.USER)
    }
}
