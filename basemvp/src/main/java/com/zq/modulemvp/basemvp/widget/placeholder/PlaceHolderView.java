package com.zq.modulemvp.basemvp.widget.placeholder;

import android.app.Activity;
import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.List;

/**
 * desc example code {
 *  *     // when view created.
 *  *     manager = PlaceHolderView.getInstance().bind(mTombstonesRv);
 *  *     // ...
 *  *     // if data-set is empty, show the place holder
 *  *     manager.showPlaceHolder(EmptyPlaceHolder.class);
 *  *
 *  *     // customize the place holder by a expose callback, {@link IPlaceHolderManager.IExpose}
 *  *     manager.showPlaceHolder(EmptyPlaceHolder.class, holder -> {
 *  *                 TextView tv = holder.findViewById(R.id.tv_place_holder);
 *  *                 tv.setText("empty text");
 *  *             });
 *  * }
 * author zhouqi
 * data 2020/6/3
 */
public class PlaceHolderView implements IPlaceHolderView {

    private Config config;
    private static volatile PlaceHolderView sInstance;

    public static PlaceHolderView getInstance() {
        if (sInstance == null) {
            throw new NullPointerException("call PlaceHolderView.Config().install() first");
        }
        return sInstance;
    }

    private PlaceHolderView() {
    }

    private PlaceHolderView(@NonNull Config config) {
        this.config = config;
    }

    @NonNull
    @Override
    public PlaceHolderManager bind(@NonNull Activity activity) {
        WrapperContext context = new WrapperContextTransformer().build(activity);
        return new PlaceHolderManager(config, context);
    }

    @NonNull
    @Override
    public PlaceHolderManager bind(@NonNull Fragment fragment) {
        WrapperContext context = new WrapperContextTransformer().build(fragment.getActivity());
        return new PlaceHolderManager(config, context);
    }

    @NonNull
    @Override
    public PlaceHolderManager bind(@NonNull View root) {
        WrapperContext context = new WrapperContextTransformer().build(root);
        return new PlaceHolderManager(config, context);
    }

    public static class Config {
        @NonNull
        private HashSet<Class<? extends PlaceHolder>> placeHolders = new HashSet<>();

        public Config addPlaceHolder(List<Class<? extends PlaceHolder>> holders) {
            placeHolders.addAll(holders);
            return this;
        }

        @NonNull
        public HashSet<Class<? extends PlaceHolder>> getPlaceHolders() {
            return placeHolders;
        }

        @MainThread
        public PlaceHolderView build() {
            return new PlaceHolderView(this);
        }

        /**
         * Generate a instance for global
         * create a custom place-holder view if the instance inconvenient by {@link #build()}
         */
        @MainThread
        public void install() {
            if (PlaceHolderView.sInstance == null) {
                synchronized (PlaceHolderView.class) {
                    if (PlaceHolderView.sInstance == null) {
                        PlaceHolderView.sInstance = new PlaceHolderView(this);
                    }
                }
            }
        }
    }
}
