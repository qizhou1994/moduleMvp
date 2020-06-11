package com.zq.modulemvp.basemvp.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zq.modulemvp.basemvp.base.Constants;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * desc
 * author zhouqi
 * data 2020/6/2
 */
public class AppUtil {

    private static Context context;

    private static volatile ConcurrentHashMap<Network, Boolean> networkInfos = new ConcurrentHashMap<>();

    public static void updateNetState(Network network, boolean available) {
        AppLog.v("updateNetState:" + network + ", available:" + available);
        networkInfos.put(network, available);
    }

    public static boolean isNetWorkAvailable(@NonNull Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = null;
        if (connectivity != null) {
            info = connectivity.getAllNetworkInfo();
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private  AppUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
         AppUtil.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }

    /**
     * View获取Activity的工具
     *
     * @param view view
     * @return Activity
     */
    public static
    @NonNull
    Activity getActivity(View view) {
        Context context = view.getContext();

        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }

        throw new IllegalStateException("View " + view + " is not attached to an Activity");
    }

    /**
     * 全局获取String的方法
     *
     * @param id 资源Id
     * @return String
     */
    public static String getString(@StringRes int id) {
        return context.getResources().getString(id);
    }

    /**
     * 判断App是否是Debug版本
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppDebug() {
        if (StringUtil.isSpace(context.getPackageName())) return false;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }


    public static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public static String getMetaDataByKey(@NonNull Context context, String key) {
        ApplicationInfo appInfo;
        AppLog.e("getMetaDataByKey key:" + key);

        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            AppLog.e("appInfo.metaData.getString(key)" + appInfo.metaData.getString(key));
            return appInfo.metaData.getString(key);

        } catch (Exception e) {
            e.printStackTrace();
        }
        AppLog.e("getMetaDataByKey key:" + key + ", value:empty");
        return "";
    }

    @NonNull
    public static String getProjectVersionName(@NonNull Context context) {
        String versionName = null;
        try {
            PackageManager pkMgr = context.getPackageManager();
            if (pkMgr != null) {
                PackageInfo pkInfo = pkMgr.getPackageInfo(context.getPackageName(), 0);
                if (pkInfo != null) {
                    versionName = pkInfo.versionName;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return TextUtils.isEmpty(versionName) ? Constants.APP.DEFAULT_VERSION_NAME : versionName;
    }

    public static boolean isInBackgroundThread() {
        return Looper.getMainLooper() != Looper.myLooper();
    }

    public static ThreadPoolExecutor getActivityBgWork(Activity activity) {
        return new ThreadPoolExecutor(1, 1,
                5, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger();

            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread t = new Thread(r);
                t.setName(activity.getClass().getSimpleName() + index.getAndIncrement());
                return t;
            }
        });
    }
}
