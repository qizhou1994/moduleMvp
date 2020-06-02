package com.zq.modulemvp.basemvp.base;

import androidx.annotation.Keep;

/**
 * desc
 * author zhouqi
 * data 2020/6/2
 */
@Keep
public interface IApplicationDelegate {

    void onCreate();

    void onTerminate();

    void onLowMemory();

    void onTrimMemory(int level);

}
