package com.fenghuo.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.zip.CRC32;

public class FileUtils {

    public static final int S_IRWXU = 00700;
    public static final int S_IRUSR = 00400;
    public static final int S_IWUSR = 00200;
    public static final int S_IXUSR = 00100;

    public static final int S_IRWXG = 00070;
    public static final int S_IRGRP = 00040;
    public static final int S_IWGRP = 00020;
    public static final int S_IXGRP = 00010;

    public static final int S_IRWXO = 00007;
    public static final int S_IROTH = 00004;
    public static final int S_IWOTH = 00002;
    public static final int S_IXOTH = 00001;

    public static final String SCHEME_ASSET = "asset";
    public static final String SCHEME_FILE = "file";
    public static final String SCHEME_ANDROID_RESOURCE = "android.resource";

    public static final String CHARSET = "utf-8";

    /**
     * mode = 777 or 752 and so on. <br />
     * mode的三个数字，分别表示owner,group,others所具备的权限。<br />
     * 1＝x 执行<br />
     * 2＝w 写 <br />
     * 4＝r 读
     *
     * @author iooly
     */
    public static enum Permission {
        PRIVATE("600"), WOLED_WRITE("666"), WOLED_READ("644"), WOLED_READ_WRITE("666");
        private String value;

        Permission(String value) {
            this.value = value;
        }
    }

    public static final void setPermission(File file, Permission permission) {
        setPermission(file, permission.value);
    }

    public static final void setPermission(File file, String permission) {
        try {
            Runtime.getRuntime().exec("chmod " + permission + " " + file.getAbsolutePath());
        } catch (IOException e) {
        }
    }

    public static boolean getFileStatus(String path, FileStatus status) {
        File file = new File(path);
        if (file.exists()) {
            status.mtime = file.lastModified();
            return true;
        }
        return false;
    }

    public static final void setPermission(String filePath, int permission) {
        setPermission(new File(filePath), permission);
    }

    public static final void setPermission(File file, int permission) {
        setPermission(file, String.format("%o", permission));
    }

    public static final boolean copyFile(File srcFile, File dstFile) {

        if (srcFile == null || dstFile == null) {
            return false;
        }

        try {
            if (srcFile.getCanonicalFile().equals(dstFile.getCanonicalFile())) {
                return false;
            }
        } catch (IOException e) {
        }

        if (srcFile.getAbsoluteFile().equals(dstFile.getAbsoluteFile())) {
            return false;
        }

        if (srcFile.getAbsolutePath().equals(dstFile.getAbsolutePath())) {
            return false;
        }

//        BufferedSource source = null;
//        Sink sink = null;
//
//        try {
//            source = Okio.buffer(Okio.source(srcFile));
//            sink = Okio.sink(dstFile);
//            source.readAll(sink);
//            return true;
//        } catch (Exception ex) {
//        } finally {
//            close(source);
//            close(sink);
//        }
        return false;

    }

    public static final boolean writeToFile(InputStream in, String destFilePath,
                                            ProgressListener listener) {
        return writeToFile(in, new File(destFilePath), listener);
    }

    public static final boolean writeToFile(InputStream in, File destFile) {
        return writeToFile(in, destFile, null);
    }

    public static final boolean writeToFile(InputStream in, File destFile,
                                            final ProgressListener listener) {
        try {
            if (destFile.exists()) {
                delete(destFile);
            }
            FileOutputStream out = new FileOutputStream(destFile);
            byte[] buffer = CommonPools.obtainByteBuffer();
            try {
                int bytesRead;
                if (listener != null) {
                    long progress = 0;
                    long t0 = SystemClock.elapsedRealtime();
                    long t1;
                    while ((bytesRead = in.read(buffer)) >= 0) {
                        out.write(buffer, 0, bytesRead);
                        progress += bytesRead;
                        t1 = SystemClock.elapsedRealtime();
                        // 100ms 刷新一次
                        if (t1 - t0 > 100) {
                            t0 = t1;
                            listener.onProgress(progress);
                        }
                    }
                    listener.onProgress(progress);
                } else {
                    while ((bytesRead = in.read(buffer)) >= 0) {
                        out.write(buffer, 0, bytesRead);
                    }
                }

            } finally {
//                CommonPools.recycle(buffer);
                sync(out);
                close(out);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Replace filename-special characters with safe characters.
     * <p/>
     * \0 \ / : * ? " < > |
     *
     * @param srcFileName unsafe file name
     * @return safe file name
     */
    public static String getSafeFileName(String srcFileName) {
        char[] characters = srcFileName.toCharArray();
        int len = characters.length;
        char c;
        for (int i = 0; i < len; i++) {
            c = characters[i];
            switch (c) {
                case '\0':
                case '\\':
                case ':':
                case '*':
                case '?':
                case '<':
                case '>':
                case '|':
                case '/': {
                    characters[i] = '_';
                    break;
                }
            }
        }

        if (len == 1 && characters[0] == '.') {
            characters[0] = '_';
        }

        if (len == 2 && characters[0] == '.' && characters[1] == '.') {
            characters[0] = '_';
            characters[1] = '_';
        }

        return new String(characters);
    }

    /**
     * <p style="color:#ff0000;font-weight:bolder;">
     * 一定要用这个函数生成缓存文件, 不然在某些系统上生成的缓存文件可能无权限访问
     * </p>
     *
     * @param context Context
     * @param prefix  String
     * @param suffix  String
     * @return File
     */
    public static File createTempFile(Context context, String prefix, String suffix) {

        File tmpFile = null;

        try {
            tmpFile = File.createTempFile(prefix, suffix);
        } catch (Exception ex) {
        }

        if (tmpFile == null) {
            File tmpDir = context.getCacheDir();
            if (tmpDir != null) {
                try {
                    tmpFile = File.createTempFile(prefix, suffix, tmpDir);
                } catch (Exception ex) {
                }
            }
        }

        if (tmpFile == null) {

            File sdcard = Environment.getExternalStorageDirectory();

            if (sdcard != null) {
                File tmpDir = new File(sdcard.getAbsoluteFile() + File.separator + "iooly"
                        + File.separator + ".tmp");
                try {
                    tmpDir.mkdirs();
                    tmpFile = File.createTempFile(prefix, suffix, tmpDir);
                } catch (Exception ex) {
                }
            }
        }

        return tmpFile;
    }

    public static boolean exists(File file) {
        return file != null && file.exists();
    }

    public static boolean exists(String filePath) {
        return filePath != null && exists(new File(filePath));
    }

    public static final boolean isFile(File file) {
        return exists(file) && file.isFile();
    }

    public static final boolean isDirectory(File file) {
        return exists(file) && file.isDirectory();
    }

    public static final void mkdirs(File dir) {
        if (dir.exists() && !dir.isDirectory()) {
            dir.delete();
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void mkdirs(String path) {
        mkdirs(new File(path));
    }

    public static void zipSrc(ArchiveSrcFile[] files, String outPath) throws IOException {
//        ZipArchiveOutputStream out = null;
//        try {
//            out = new ZipArchiveOutputStream(openOutputStream(outPath));
//            out.setEncoding(CHARSET);
//            out.setMethod(ZipEntry.STORED);
//            CRC32 crc = new CRC32();
//            byte[] buffer = CommonPools.obtainByteBuffer();
//            try {
//                for (ArchiveSrcFile file : files) {
//                    zipEntry(file, out, crc, buffer);
//                }
//            } finally {
//                CommonPools.recycle(buffer);
//            }
//        } finally {
//            close(out);
//        }
    }

//    public static void writeBeanList(List<? extends Bean> list, File file) throws IOException {
//        FileOutputStream outputStream = null;
//        try {
//            outputStream = new FileOutputStream(file);
//            writeBeanList(list, outputStream);
//            outputStream.flush();
//        } finally {
//            sync(outputStream);
//            close(outputStream);
//        }
//    }
//
//    public static void writeBeanList(List<? extends Bean> list, OutputStream outputStream)
//            throws IOException {
//        JsonWriter writer = new JsonWriter(new ChinaUnicodeWriter(
//                new OutputStreamWriter(outputStream, Constant.CHARSET)));
//        writer.beginObject();
//        writer.name("len").value(list.size());
//        writer.name("data");
//        Bean.writeToJson(list, writer);
//        writer.endObject();
//        writer.flush();
//    }
//
//    public static <T extends Bean> List<T> readBeanList(Class<T> clazz, File file)
//            throws IOException {
//        InputStream inputStream = null;
//        List<T> list = null;
//        try {
//            inputStream = openInputStream(file);
//            list = readBeanList(clazz, inputStream);
//        } finally {
//            close(inputStream);
//        }
//        return list;
//    }
//
//    public static <T extends Bean> List<T> readBeanList(
//            Class<T> clazz, InputStream inputStream) throws IOException {
//        JsonReader reader = new JsonReader(new InputStreamReader(inputStream, Constant.CHARSET));
//        List<T> list = null;
//        String tagName;
//        reader.beginObject();
//        while (reader.hasNext()) {
//            tagName = reader.nextName();
//            if ("data".equals(tagName)) {
//                list = Bean.readListFromJSON(reader, clazz);
//            } else {
//                reader.skipValue();
//            }
//        }
//        reader.endObject();
//        return list;
//    }
//
//    public static boolean zipSrcSafe(ArchiveSrcFile[] files, String outPath) {
//        try {
//            zipSrc(files, outPath);
//            return true;
//        } catch (Exception ex) {
//            DebugLog.e("test_zip", Log.getStackTraceString(ex));
//        }
//        return false;
//    }
//
//    private static void zipEntry(ArchiveSrcFile file, ZipArchiveOutputStream out, CRC32 crc,
//                                 byte[] buff) throws IOException {
//        if (file.srcFile == null || !file.srcFile.exists()) {
//            throw new FileNotFoundException("ArchiveSrcFile : " + file + " srcFile not found.");
//        }
//
//        if (file.srcFile.isFile()) {
//            zipFileEntry(file, out, crc, buff);
//        } else {
//            zipDirEntry(file, out, crc, buff);
//        }
//    }
//
//    private static void zipDirEntry(ArchiveSrcFile file, ZipArchiveOutputStream out, CRC32 crc,
//                                    byte[] buff) throws IOException {
//        File[] files = file.srcFile.listFiles();
//        if (files != null && files.length > 0) {
//            String path = file.destName + File.separator;
//            for (File f : files) {
//                ArchiveSrcFile asf = new ArchiveSrcFile(f, path + f.getName());
//                zipEntry(asf, out, crc, buff);
//            }
//        }
//    }
//
//    private static void zipFileEntry(ArchiveSrcFile file, ZipArchiveOutputStream out, CRC32 crc,
//                                     byte[] buff) throws IOException {
//        calculateCrc(file.srcFile, crc, buff);
//        FileInputStream in = null;
//        try {
//            in = new FileInputStream(file.srcFile);
//            ZipArchiveEntry entry = (ZipArchiveEntry) out
//                    .createArchiveEntry(file.srcFile, file.destName);
//            entry.setMethod(ZipEntry.STORED);
//            entry.setCrc(crc.getValue());
//            entry.setSize(file.srcFile.length());
//            entry.setCompressedSize(file.srcFile.length());
//
//            out.putArchiveEntry(entry);
//            int count;
//            crc.reset();
//            while ((count = in.read(buff)) != -1) {
//                out.write(buff, 0, count);
//            }
//            out.closeArchiveEntry();
//        } finally {
//            close(in);
//        }
//    }

    private static void calculateCrc(File file, CRC32 crc, byte[] buff) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            int count;
            crc.reset();
            while ((count = in.read(buff)) != -1) {
                crc.update(buff, 0, count);
            }
        } finally {
            close(in);
        }
    }

//    public static void zipSrc(List<ArchiveSrcFile> files, String outPath) throws IOException {
//        ZipArchiveOutputStream out = null;
//        byte buff[] = CommonPools.obtainByteBuffer();
//        try {
//            out = new ZipArchiveOutputStream(openOutputStream(outPath));
//            out.setEncoding(CHARSET);
//            out.setMethod(ZipEntry.STORED);
//            CRC32 crc = new CRC32();
//            for (ArchiveSrcFile file : files) {
//                zipEntry(file, out, crc, buff);
//            }
//        } finally {
//            CommonPools.recycle(buff);
//            close(out);
//        }
//    }

    public static boolean zipSrcSafe(List<ArchiveSrcFile> files, String outPath) {
//        try {
//            zipSrc(files, outPath);
//            return true;
//        } catch (Exception ex) {
//            DebugLog.e("test_zip", Log.getStackTraceString(ex));
//        }
        return false;
    }

    public static boolean zip(List<File> files, String outPath) {
//        int len = files.size();
//        ArchiveSrcFile[] srcFiles = new ArchiveSrcFile[len];
//        ArchiveSrcFile srcFile;
//        File file;
//        for (int i = 0; i < len; i++) {
//            srcFile = new ArchiveSrcFile();
//            file = files.get(i);
//            srcFile.srcFile = file;
//            srcFile.destName = file.getName();
//            srcFiles[i] = srcFile;
//        }
//        return zipSrcSafe(srcFiles, outPath);
        return false;
    }

    public static boolean zip(File[] files, String outPath) {
//        int len = files.length;
//        ArchiveSrcFile[] srcFiles = new ArchiveSrcFile[len];
//        ArchiveSrcFile srcFile;
//        File file;
//        for (int i = 0; i < len; i++) {
//            srcFile = new ArchiveSrcFile();
//            file = files[i];
//            srcFile.srcFile = file;
//            srcFile.destName = file.getName();
//            srcFiles[i] = srcFile;
//        }
//        return zipSrcSafe(srcFiles, outPath);
        return false;
    }

    public static void unZip(InputStream in, String outPath, boolean isCover) throws IOException {
//        ArchiveInputStream inZip = null;
//        String szName = "";
//        inZip = new ZipArchiveInputStream(in);
//        ArchiveEntry zipEntry;
//        while ((zipEntry = inZip.getNextEntry()) != null) {
//            szName = zipEntry.getName();
//            if (!zipEntry.isDirectory()) {
//                File file = new File(outPath + File.separator + szName);
//                if (file.exists()) {
//                    if (isCover) {
//                        file.delete();
//                    } else {
//                        continue;
//                    }
//                }
//                File folder = file.getParentFile();
//                if (!folder.exists()) {
//                    folder.mkdirs();
//                }
//                writeToFile(inZip, file);
//            }
//        }
    }

    public static InputStream openInputStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public static InputStream openInputStream(Context context, Uri uri)
            throws FileNotFoundException {
        String scheme = uri.getScheme();
        InputStream in = null;
        try {
            switch (scheme) {
                case SCHEME_ASSET: {
                    try {
                        in = context.getAssets().open(uri.getHost() + uri.getPath());
                    } catch (Exception ex1) {
                        FileNotFoundException ex2 = new FileNotFoundException("Asset File Not Found: " + uri);
                        ex2.initCause(ex1);
                    }
                    break;
                }
                case SCHEME_FILE: {
                    try {
                        in = new FileInputStream(uri.getPath());
                    } catch (Exception ex1) {
                        FileNotFoundException ex2 = new FileNotFoundException("File Not Found: " + uri);
                        ex2.initCause(ex1);
                    }
                    break;
                }
                case SCHEME_ANDROID_RESOURCE: {
                    OpenResourceIdResult r = getResourceId(context, uri);
                    try {
                        in = r.r.openRawResource(r.id);
                    } catch (Exception ex1) {
                        FileNotFoundException ex2 = new FileNotFoundException("File Not Found: " + uri);
                        ex2.initCause(ex1);
                        throw new FileNotFoundException("Resource does not exist: " + uri);
                    }
                    break;
                }

            }
        } catch (FileNotFoundException ex) {
            Log.e("test_image", Log.getStackTraceString(ex));
            close(in);
            in = null;
        }
        if (in == null) {
            in = context.getContentResolver().openInputStream(uri);
        }
        return in;
    }

    public static Reader openReader(File file) throws UnsupportedEncodingException,
            FileNotFoundException {
        return new InputStreamReader(openInputStream(file), CHARSET);
    }

    public static OutputStream openOutputStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    public static Writer openWriter(File file) throws UnsupportedEncodingException,
            FileNotFoundException {
        return new OutputStreamWriter(openOutputStream(file), CHARSET);
    }

    public static OutputStream openOutputStream(String filePath) throws FileNotFoundException {
        return new FileOutputStream(filePath);
    }

    public static InputStream openInputStream(String filePath) throws FileNotFoundException {
        return new FileInputStream(filePath);
    }

    public static Reader newReader(String filePath) throws FileNotFoundException {
        return new InputStreamReader(openInputStream(filePath));
    }

    public static Reader newReader(File file) throws FileNotFoundException {
        return new InputStreamReader(openInputStream(file));
    }

    public static Writer newWriter(String filePath) throws FileNotFoundException {
        return new OutputStreamWriter(openOutputStream(filePath));
    }

    public static Writer newWriter(File file) throws FileNotFoundException {
        return new OutputStreamWriter(openOutputStream(file));
    }

    public static Writer newHexWriter(File file) throws IOException {
//        return new OutputStreamWriter(new HexOutputStream(openOutputStream(file)));
        return null;
    }

//    public static boolean writeJSONToFile(File file, JsonElement je) {
//        Writer writer = null;
//        try {
//            writer = openWriter(file);
//            Bean.writeJson(je, writer);
//        } catch (Exception e) {
//        } finally {
//            close(writer);
//        }
//        return false;
//    }
//
//    public static boolean writeJSONToFile(String filePath, JsonElement je) {
//        return writeJSONToFile(new File(filePath), je);
//    }
//
//    public static JsonElement readJSONFromFile(String filePath) {
//        return readJSONFromFile(new File(filePath));
//    }
//
//    public static JsonElement readJSONFromFile(File file) {
//        Reader reader = null;
//        try {
//            return Bean.parseJson(reader = openReader(file));
//        } catch (Exception ex) {
//        } finally {
//            close(reader);
//        }
//        return null;
//    }
//
//    public static <T extends Bean> List<T> readJsonData(File file, final Class<T> clazz) {
//        Reader reader = null;
//        try {
//            reader = new FileReader(file);
//            return readJsonData(reader, clazz);
//        } catch (Exception ex) {
//        } finally {
//            close(reader);
//        }
//        return null;
//    }
//
//    public static <T extends Bean> List<T> readJsonData(Reader reader, final Class<T> clazz) {
//        final List<T> list = new ArrayList<>();
//        try {
//            readJson(reader, new OnJsonNameListener() {
//                @Override
//                public boolean onName(JsonReader reader, String name) throws IOException {
//                    boolean result = false;
//                    if ("data".equals(name)) {
//                        List<T> l = Bean.readListFromJSON(reader, clazz);
//                        if (l != null && l.size() > 0) {
//                            list.addAll(l);
//                        }
//                        result = true;
//                    }
//                    return result;
//                }
//            });
//        } catch (IOException ex) {
//        }
//        return list;
//    }
//
//    public static void readJson(Reader reader, OnJsonNameListener listener) throws IOException {
//        JsonReader jsonReader = new JsonReader(reader);
//        jsonReader.beginObject();
//        while (jsonReader.hasNext()) {
//            String name = jsonReader.nextName();
//            if (name == null) {
//                jsonReader.skipValue();
//                continue;
//            }
//            if (!listener.onName(jsonReader, name)) {
//                jsonReader.skipValue();
//            }
//        }
//        jsonReader.endObject();
//    }

//    public static void readJson(File file, OnJsonNameListener listener) throws IOException {
//        Reader reader = null;
//        try {
//            reader = new FileReader(file);
//            readJson(reader, listener);
//        } finally {
//            close(reader);
//        }
//    }
//
//    public static JsonElement readJSONFromFile(InputStream in) throws IOException {
//        return Bean.parseJson(readFileToString(in));
//    }
//
//    public static boolean writeStringToFile(String filePath, String string) {
//        return writeStringToFile(new File(filePath), string);
//    }
//
//    public static boolean writeBeanToFile(File file, Bean bean) {
//        if (bean == null) {
//            return false;
//        }
//        JsonWriter writer = null;
//        try {
//            writer = new JsonWriter(openWriter(file));
//            bean.writeToJSON(writer);
//            return true;
//        } catch (Exception ex) {
//        } finally {
//            close(writer);
//        }
//        return false;
//
//    }

//    public static <T extends Bean> T readBeanFromFile(File file, Class<T> clazz) {
//        InputStream in = null;
//        try {
//            in = openInputStream(file);
//            return readBean(clazz, in);
//        } catch (Exception ex) {
//        } finally {
//            close(in);
//        }
//        return null;
//    }
//
//    public static <T extends Bean> T readBeanFromHexFile(File file, Class<T> clazz) {
//        InputStream in = null;
//        try {
//            in = new HexInputStream(openInputStream(file));
//            return readBean(clazz, in);
//        } catch (Exception ex) {
//        } finally {
//            close(in);
//        }
//        return null;
//    }
//
//    public static <T extends Bean> T readBean(Class<T> clazz, InputStream inputStream)
//            throws UnsupportedEncodingException {
//        JsonReader reader = null;
//        try {
//            reader = new JsonReader(new InputStreamReader(inputStream, Constant.CHARSET));
//            return Bean.readFromJSON(reader, clazz);
//        } finally {
//            close(reader);
//        }
//    }

    public static boolean writeStringToFile(File file, String string) {
        Writer writer = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(newWriter(file));
            writer.write(string);
            writer.flush();
            return true;
        } catch (Exception ex) {
        } finally {
            close(writer);
        }
        return false;
    }

    public static boolean writeStringToHexFile(File file, String string) {
        Writer writer = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(newHexWriter(file));
            writer.write(string);
            writer.flush();
            return true;
        } catch (Exception ex) {
        } finally {
            close(writer);
        }
        return false;
    }

    public static String readFileToString(File file) {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            return readFileToString(in);
        } catch (Exception e) {
        } finally {
            close(in);
        }
        return null;
    }

    public static String readFileToString(InputStream in) throws IOException {

        Reader reader = new InputStreamReader(in);
        char[] cb = CommonPools.obtainCharBuffer();
        StringBuilder result = CommonPools.obtainStringBuilder();
        int len;
        try {
            while ((len = reader.read(cb)) != -1) {
                result.append(cb, 0, len);
            }
            return result.toString();
        } finally {
            CommonPools.recycle(result);
            CommonPools.recycle(cb);
        }
    }

    public static void clearDir(File dir) {
        if (!isDirectory(dir)) {
            return;
        }

        File[] files = dir.listFiles();

        if (files == null || files.length <= 0) {
            return;
        }

        for (File file : files) {
            try {
                delete(file);
            } catch (Exception e) {
            }
        }
    }

    public static String readFileToString(String filePath) {
        return readFileToString(new File(filePath));
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ex) {
            }
        }
    }

    public static boolean delete(String path) {
        return !TextUtils.isEmpty(path) && delete(new File(path));
    }

    public static boolean checkDir(String dirPath) {
        return checkDir(new File(dirPath));
    }

    public static boolean checkDir(File dir) {
        if (dir == null) {
            return false;
        }
        boolean result;
        if (!dir.exists()) {
            result = dir.mkdirs();
        } else {
            result = dir.isDirectory() || (FileUtils.delete(dir) && dir.mkdirs());
        }
        return result;
    }

    public static boolean delete(File f) {

        if (f == null || !f.exists()) {
            return false;
        }

        try {
            return deleteInternal(f);
        } catch (Exception ex) {
        }
        return false;
    }

    private static boolean deleteInternal(File f) throws IOException {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (!deleteInternal(file)) {
                        return false;
                    }
                }
            }
            return f.delete();
        } else {
            return f.delete();
        }
    }

    public static void copyToFilesDir(Context context, File srcFile, String dstFileName) {
        File dstFile = new File(context.getFilesDir(), dstFileName);
        if (dstFile.exists()) {
            try {
                delete(dstFile);
            } catch (Exception e) {
            }
        }
        copyFile(srcFile, dstFile);
    }

    public static void sync(FileOutputStream out) {
        if (out != null) {
            try {
                out.flush();
            } catch (IOException e1) {
            }
            try {
                out.getFD().sync();
            } catch (Exception e) {
            }
        }
    }

    public static class ArchiveSrcFile {
        public File srcFile;
        public String destName;

        public ArchiveSrcFile() {
        }

        public ArchiveSrcFile(File file) {
            srcFile = file;
            destName = file.getName();
        }

        public ArchiveSrcFile(File file, String name) {
            srcFile = file;
            destName = name;
        }

        @Override
        public String toString() {
            return "ArchiveSrcFile{" +
                    "srcFile=" + srcFile.getAbsolutePath() +
                    ", destName='" + destName + '\'' +
                    '}';
        }
    }

    public static boolean isSDCardExists() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public long getSDAllSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 获取所有数据块数
        long allBlocks = sf.getBlockCount();
        // 返回SD卡大小
        // return allBlocks * blockSize; //单位Byte
        // return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    public long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        // return freeBlocks * blockSize; //单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    private static OpenResourceIdResult getResourceId(Context context, Uri uri) throws FileNotFoundException {
        String authority = uri.getAuthority();
        Resources r = null;
        if (TextUtils.isEmpty(authority)) {
            throw new FileNotFoundException("No authority: " + uri);
        } else {
            try {
                PackageManager pm = context.getPackageManager();
                if (pm != null) {
                    r = pm.getResourcesForApplication(authority);
                }
            } catch (PackageManager.NameNotFoundException ex) {
                throw new FileNotFoundException("No package found for authority: " + uri);
            }
        }
        List<String> path = uri.getPathSegments();
        if (path == null) {
            throw new FileNotFoundException("No path: " + uri);
        }
        int len = path.size();
        int id;
        if (len == 1) {
            try {
                id = Integer.parseInt(path.get(0));
            } catch (NumberFormatException e) {
                throw new FileNotFoundException("Single path segment is not a resource ID: " + uri);
            }
        } else if (len == 2) {
            id = r.getIdentifier(path.get(1), path.get(0), authority);
        } else {
            throw new FileNotFoundException("More than two path segments: " + uri);
        }
        if (id == 0) {
            throw new FileNotFoundException("No resource found for: " + uri);
        }
        OpenResourceIdResult res = new OpenResourceIdResult();
        res.r = r;
        res.id = id;
        return res;
    }

    private static class OpenResourceIdResult {
        public Resources r;
        public int id;
    }

    /**
     * File status information. This class maps directly to the POSIX stat structure.
     */
    public static final class FileStatus {
        public int dev;
        public int ino;
        public int mode;
        public int nlink;
        public int uid;
        public int gid;
        public int rdev;
        public long size;
        public int blksize;
        public long blocks;
        public long atime;
        public long mtime;
        public long ctime;
    }

}
