package com.zq.modulemvp.basemvp.base;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Process;
import android.os.StrictMode;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;
import com.zq.modulemvp.basemvp.AppData;
import com.zq.modulemvp.basemvp.R;
import com.zq.modulemvp.basemvp.util.ActivityStackUtil;
import com.zq.modulemvp.basemvp.util.AppUtil;
import com.zq.modulemvp.basemvp.util.ClassUtils;
import com.zq.modulemvp.basemvp.util.ModuleApp;
import com.zq.modulemvp.basemvp.widget.placeholder.EmptyPlaceHolder;
import com.zq.modulemvp.basemvp.widget.placeholder.ErrorPlaceHolder;
import com.zq.modulemvp.basemvp.widget.placeholder.PlaceHolder;
import com.zq.modulemvp.basemvp.widget.placeholder.PlaceHolderView;
import com.zq.modulemvp.common.BuildConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.AutoSizeConfig;
import me.yokeyword.fragmentation.Fragmentation;


/**
 * desc  要想使用BaseApplication，必须在组件中实现自己的Application，并且继承BaseApplication；
 *  * 组件中实现的Application必须在debug包中的AndroidManifest.xml中注册，否则无法使用；
 *  * 组件的Application需置于java/debug文件夹中，不得放于主代码；
 *  * 组件中获取Context的方法必须为:Utils.getContext()，不允许其他写法；
 * author zhouqi
 * data 2020/6/2
 */
public class BaseApplication extends Application {

    public static final String ROOT_PACKAGE = "com.zq.modulemvp";

    private ConnectivityManager.NetworkCallback mNetCallback = new NetCallback();



    @Override
    public void onCreate() {
        super.onCreate();

        Logger.init("pattern").logLevel(LogLevel.FULL);
        AppUtil.init(this);
        AppData.initContext(this);
        MMKV.initialize(this);
        final boolean isMainProcess = isMainProcess();
        initARouter(isMainProcess);
        initDebugComponents(isMainProcess);
        initRefreshHeaderFooter();
        regNetworkCallback();
        initBugly(isMainProcess);
        if (isMainProcess) {
            ModuleApp.initContext(this);
            ActivityStackUtil.getInstance().init(this);
        }
        MMKV.initialize(this);
        if (BuildConfig.AUTO_SIZE) {
            configAutoSize();
        }

        initPlaceHolder();

    }

    /**
     * enable fragment debug
     */
    private void initDebugComponents(boolean isMainProcess) {
        if (!isMainProcess) {
            return;
        }
        if (BuildConfig.DEBUG) {
            Fragmentation.builder()
                    // show stack view. Mode: BUBBLE, SHAKE, NONE
                    .stackViewMode(Fragmentation.BUBBLE)
                    .debug(true)
                    .install();
            LeakCanary.install(this);
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    /* API等级11，使用StrictMode.noteSlowCode */
                    .detectCustomSlowCalls()
                    .detectDiskReads()
                    .detectDiskWrites()
                    /* or .detectAll() for all detectable problems */
                    .detectNetwork()
                    /* 弹出违规提示对话框 */
                    /*.penaltyDialog()*/
                    /* 在Logcat 中打印违规异常信息 */
                    .penaltyLog()
                    /* API等级11 */
                    .penaltyFlashScreen()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    /* API等级11 */
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    /*.penaltyDeath()*/
                    .build());
        }
    }

    private void initRefreshHeaderFooter() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
            return new MaterialHeader(context);
        });

        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }

    private void initBugly(boolean isMainProcess) {
        if (!isMainProcess) {
            return;
        }
        final boolean isDebug = BuildConfig.DEBUG;
        Context context = getApplicationContext();
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(true);
        // 初始化Bugly
        CrashReport.initCrashReport(context, BuildConfig.BUG_APP_ID, isDebug, strategy);
        // 如果通过“AndroidManifest.xml”来配置APP信息，初始化方法如下
        // CrashReport.initCrashReport(context, strategy);
    }

    /**
     * Global place holder Empty list/ Net error
     */
    private void initPlaceHolder() {
        // TODO check if main process only
        ArrayList<Class<? extends PlaceHolder>> holders = new ArrayList<>();
        holders.add(EmptyPlaceHolder.class);
        holders.add(ErrorPlaceHolder.class);
        new PlaceHolderView.Config().addPlaceHolder(holders).install();
    }

    private void configAutoSize() {
        AutoSizeConfig.getInstance().setLog(BuildConfig.DEBUG);
        AutoSizeConfig.getInstance().setCustomFragment(true);
    }


    private void initARouter(boolean isMainProcess) {
        if (!isMainProcess) {
            return;
        }
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }


    private boolean isMainProcess() {
        Context context = getApplicationContext();
        String newProcess = getProcessName(Process.myPid());
        return context.getPackageName().equals(newProcess);
    }

    /**
     * register network connectivity callback
     */
    private void regNetworkCallback() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return;
        }
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        // after API-21
        cm.registerNetworkCallback(builder.build(), mNetCallback);
    }

    /**
     * Listening the network state, update immediately
     */
    private static class NetCallback extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(@NonNull Network network) {
            AppUtil.updateNetState(network, true);
        }

        @Override
        public void onLost(@NonNull Network network) {
            AppUtil.updateNetState(network, false);
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities capabilities) {
            super.onCapabilitiesChanged(network, capabilities);
            if (Build.VERSION.SDK_INT >= 23) {
                AppUtil.updateNetState(network,
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED));
            } else {
                AppUtil.updateNetState(network,
                        AppUtil.isNetWorkAvailable(AppUtil.getContext()));
            }
        }
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

}