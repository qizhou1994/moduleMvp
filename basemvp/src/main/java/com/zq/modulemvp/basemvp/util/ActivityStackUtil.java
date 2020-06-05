package com.zq.modulemvp.basemvp.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import java.util.Stack;

/**
 * desc
 * author zhouqi
 * data 2020/6/4
 */
public class ActivityStackUtil implements Application.ActivityLifecycleCallbacks {
    private Stack<Activity> mActivityStack = new Stack<>();

    private static class Holder {
        private static ActivityStackUtil sInstance = new ActivityStackUtil();
    }

    public static ActivityStackUtil getInstance() {
        return Holder.sInstance;
    }


    public void init(Application application) {
        application.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivityStack.push(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivityStack.remove(activity);
    }

    public FragmentActivity getPreActivity(Activity mCurrentActivity) {
        Activity preActivity = null;
        if (mActivityStack.size() > 1) {
            int index = mActivityStack.lastIndexOf(mCurrentActivity);
            if (index > 0) {
                preActivity = mActivityStack.get(index - 1);
            } else {
                preActivity = mActivityStack.lastElement();
            }
        }
        if (!(preActivity instanceof FragmentActivity)) {
            return null;
        }
        return (FragmentActivity) preActivity;
    }

    public Activity getTopActivity() {
        int size = mActivityStack.size();
        if (size > 0) {
            return mActivityStack.get(size - 1);
        }
        return null;
    }
}