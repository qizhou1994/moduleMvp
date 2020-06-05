package com.zq.modulemvp.basemvp.util;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * desc
 * author zhouqi
 * data 2020/6/4
 */
public class ModuleApp {
    private ModuleApp() {}

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
