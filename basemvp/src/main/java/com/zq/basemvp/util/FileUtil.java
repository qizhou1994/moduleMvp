package com.zq.basemvp.util;

import com.zq.modulemvp.basemvp.util.AppLog;

import java.io.File;
import java.io.IOException;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class FileUtil {
    private FileUtil() {
    }

    public static File createFile(String name) throws IOException {
        return createFile(new File(name));
    }

    public static File createFile(File file) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
            AppLog.w("create file failed" + file);
        }
        return file;
    }

    public static File createFile(String dir, String name) throws IOException {
        File parent = new File(dir);
        if (!parent.isDirectory()) {
            parent.mkdirs();
        }
        return createFile(new File(dir, name));
    }

    public static File createFile(File parent, String name) throws IOException {
        if (!parent.isDirectory()) {
            parent.mkdirs();
        }
        return createFile(new File(parent, name));
    }
}
