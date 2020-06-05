package com.zq.modulemvp.basemvp.widget.placeholder;

import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * desc
 * author zhouqi
 * data 2020/6/3
 */
public interface IPlaceHolderManager {
    interface IExpose {
        /**
         * expose the place holder to callers.
         * @param placeHolder instance
         */
        void expose(@NonNull View placeHolder);
    }

    /**
     * show a place-holder
     *
     * @param clz
     */
    @MainThread
    void showPlaceHolder(@NonNull Class<? extends PlaceHolder> clz);


    /**
     *
     * expose the placeholder view to outside, customize easily.
     * @param clz holder class
     * @param expose a callback before holder showing to user.
     */
    @MainThread
    void showPlaceHolder(@NonNull Class<? extends PlaceHolder> clz,
                         @Nullable IExpose expose);

    /**
     * hide the place holder
     */
    @MainThread
    void hidePlaceHolder();

    /**
     * reset the layout state, before {@link PlaceHolderView#bind(View)}.
     * Can not use {@link #showPlaceHolder(Class)} after {@code #reset()} called.
     */
    @MainThread
    void reset();
}
