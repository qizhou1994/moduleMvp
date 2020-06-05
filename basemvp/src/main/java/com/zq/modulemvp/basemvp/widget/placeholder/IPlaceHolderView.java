package com.zq.modulemvp.basemvp.widget.placeholder;

import android.app.Activity;
import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * desc
 * author zhouqi
 * data 2020/6/3
 */
public interface IPlaceHolderView {
    /**
     * activity content view
     *
     * @param activity activity
     */
    @CheckResult
    @NonNull
    PlaceHolderManager bind(@NonNull Activity activity);

    /**
     * fragment content view
     *
     * @param fragment fragment
     */
    @CheckResult
    @NonNull
    PlaceHolderManager bind(@NonNull Fragment fragment);

    /**
     * a single view
     *
     * @param target target view will bind to
     */
    @CheckResult
    @NonNull
    PlaceHolderManager bind(@NonNull View target);
}
