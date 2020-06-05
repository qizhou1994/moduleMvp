package com.zq.modulemvp.basemvp.util;

import android.util.Log;

import com.zq.modulemvp.basemvp.BuildConfig;

import java.util.Locale;

/**
 * desc
 * author zhouqi
 * data 2020/6/3
 */
public class AppLog {
    private AppLog() {}
    private static boolean ENABLE_DEBUG = BuildConfig.DEBUG;
    private static boolean ENABLE_ERROR = true;

    public static String TAG = "Smpt";

    public static void setTAG(String tag) {
        TAG = tag;
    }

    public static void enableDebug(boolean enable) {
        ENABLE_DEBUG = enable;
    }

    public static boolean isEnableDebug() {
        return ENABLE_DEBUG;
    }

    public static void enableError(boolean enable) {
        ENABLE_ERROR = enable;
    }

    private static String getTag() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String callingClass = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(AppLog.class)) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                break;
            }
        }
        if (callingClass.contains("$")) {
            int end = callingClass.lastIndexOf("$");
            callingClass = callingClass.substring(0, end);
        }
        return callingClass + ": ";
    }

    private static String buildMessage(String msg) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String caller = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(AppLog.class)) {
                caller = trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[t=%d] %s: %s",
                Thread.currentThread().getId(), caller, msg);
    }

    public static void v(String msg) {
        if (ENABLE_DEBUG) {
            Log.v(TAG, getTag() + buildMessage(msg));
        }
    }

    public static void d(String msg) {
        if (ENABLE_DEBUG) {
            Log.d(TAG, getTag() + buildMessage(msg));
        }
    }

    public static void i(String msg) {
        if (ENABLE_DEBUG) {
            Log.i(TAG, getTag() + buildMessage(msg));
        }
    }

    public static void w(String msg) {
        if (ENABLE_DEBUG) {
            Log.w(TAG, getTag() + buildMessage(msg));
        }
    }

    public static void e(String msg) {
        if (ENABLE_ERROR) {//Different between other.
            Log.e(TAG, getTag() + buildMessage(msg));
        }
    }

    public static void e(String msg, Throwable e) {
        if (ENABLE_ERROR) {//Different between other.
            Log.e(TAG, getTag() + buildMessage(msg), e);
        }
    }

    public static void printTraceStack(String msg) {
        if (ENABLE_DEBUG) {
            Exception e = new Exception();
            StackTraceElement[] steArray = e.getStackTrace();
            Log.i(TAG, "---------------- Stack Trace ---------------");
            Log.i(TAG, "" + msg);
            for (StackTraceElement ste : steArray) {
                Log.i(TAG, ste.toString());
            }
        }
    }

    public static long currentTimeMillis() {
        long time = System.currentTimeMillis();// ms
        return time;
    }

    public static void duration(String msg, long start, long end) {
        Log.d(TAG, buildMessageThread(msg) + " , duration: " + (end - start) + "ms");
    }

    private static String buildMessageThread(String msg) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String caller = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(AppLog.class)) {
                caller = trace[i].getMethodName();
                break;
            }
        }
        Thread thread = Thread.currentThread();
        return String.format(Locale.US, "%s (%s): %s", thread.toString(), caller, msg);
    }
}
