package com.zq.basemvp.util;

import java.io.Closeable;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class IOUtils {
    private IOUtils() {}
    public static void closeSilence(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception ignore) {}
        }
    }
}
