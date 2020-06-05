package com.zq.modulemvp.basemvp.widget.placeholder;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

/**
 * desc
 * author zhouqi
 * data 2020/6/4
 */
public abstract class PlaceHolder {
    private static final String TAG = PlaceHolder.class.getName();

    private View placeHolder;
    private Context context;

    @NonNull
    public View getPlaceHolder() {
        int resId = onCreateView();
        if (placeHolder == null) {
            if (context == null) {
                Log.e(TAG, "call setContext() before getPlaceHolder()");
            }
            placeHolder = View.inflate(context, resId, null);
        }
        onViewCreated(context, placeHolder);
        return placeHolder;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * get layout content id
     *
     * @return PlaceHolder Layout Id
     */
    @CheckResult
    @LayoutRes
    public abstract int onCreateView();

    /**
     * like fragment
     *
     * @param context ctx
     * @param view the place holder layout
     */
    public abstract void onViewCreated(Context context, View view);

    /**
     * attach to parent
     */
    public abstract void onAttach();

    /**
     * detach from parent, It's a good time to release resources.
     */
    public abstract void onDetach();
}
