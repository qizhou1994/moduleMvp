package com.zq.modulemvp.basemvp;

import android.content.Context;

import androidx.annotation.NonNull;

import com.zq.modulemvp.basemvp.util.AppUtil;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class AppData {
    private AppData() {}

    private static Context mAppContext;
    private static boolean isDebugMode = true;
    private static String sVersionName;
    private static String sSfApiToken;

    public static void initContext(Context base) {
        mAppContext = base;
        sVersionName = AppUtil.getProjectVersionName(base);
    }

    public static boolean isDebugMode(){
        return isDebugMode;
    }

    public static void onCreate(boolean debugMode) {
        isDebugMode = debugMode;
    }

    @NonNull
    public static Context getAPPContext() {
        return mAppContext;
    }

    @NonNull
    public static String getVersionName() {
        return sVersionName;
    }

    public static void setToken(String token) {
        sSfApiToken = token;
    }

    public static String getToken() {
        return sSfApiToken;
    }
}
