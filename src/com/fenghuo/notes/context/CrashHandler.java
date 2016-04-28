package com.fenghuo.notes.context;

import android.content.Context;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.support.v4.BuildConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by gang on 16-4-28.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    Context context;

    CrashHandler(Context context) {
        this.context = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        File dir = context.getExternalFilesDir("logs");
        File file = new File(dir, "crash.log");
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            //记录手机信息
            writeInformation(writer);
            //记录log信息
            writer.write("\n\n");
            writeCrash(ex, writer);
            writer.flush();
            writer.close();
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SystemClock.sleep(200);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    //log信息
    private void writeCrash(Throwable ex, FileWriter writer) {
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
    }

    //手机信息
    private void writeInformation(FileWriter fw) throws IOException {
        fw.write("pkg_name=" + BuildConfig.APPLICATION_ID);
        fw.write("\nversionCode=" + BuildConfig.VERSION_CODE);
        fw.write("\nversionName=" + BuildConfig.VERSION_NAME);
        fw.write("\ntimestamp=" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()));
        fw.write("\nboard=" + SystemProperties.get("ro.product.board", "unknown"));
        fw.write("\nbootloader=" + SystemProperties.get("ro.bootloader", "unknown"));
        fw.write("\nbrand=" + SystemProperties.get("ro.product.brand", "unknown"));
        fw.write("\ncpu_abi=" + SystemProperties.get("ro.product.cpu.abi", "unknown"));
        fw.write("\ncpu_abi2=" + SystemProperties.get("ro.product.cpu.abi2", "unknown"));
        fw.write("\ndevice=" + SystemProperties.get("ro.product.device", "unknown"));
        fw.write("\ndisplay=" + SystemProperties.get("ro.build.display.id", "unknown"));
        fw.write("\nfingerprint=" + SystemProperties.get("ro.build.fingerprint", "unknown"));
        fw.write("\nhardware=" + SystemProperties.get("ro.hardware", "unknown"));
        fw.write("\nhost=" + SystemProperties.get("ro.build.host", "unknown"));
        fw.write("\nid=" + SystemProperties.get("ro.build.id", "unknown"));
        fw.write("\nmanufacturer=" + SystemProperties.get("ro.product.manufacturer", "unknown"));
        fw.write("\nmodel=" + SystemProperties.get("ro.product.model", "unknown"));
        fw.write("\nproduct=" + SystemProperties.get("ro.product.name", "unknown"));
        fw.write("\nradio=" + SystemProperties.get("gsm.version.baseband", "unknown"));
        fw.write("\ntags=" + SystemProperties.get("ro.build.tags", "unknown"));
        fw.write("\ntype=" + SystemProperties.get("ro.build.type", "unknown"));
        fw.write("\nuser=" + SystemProperties.get("ro.build.user", "unknown"));
        fw.write("\ncodename=" + SystemProperties.get("ro.build.version.codename", "unknown"));
        fw.write("\nincremental=" + SystemProperties.get("ro.build.version.incremental", "unknown"));
        fw.write("\nrelease=" + SystemProperties.get("ro.build.version.release", "unknown"));
        fw.write("\nsdk=" + SystemProperties.get("ro.build.version.sdk", "unknown"));
    }
}
