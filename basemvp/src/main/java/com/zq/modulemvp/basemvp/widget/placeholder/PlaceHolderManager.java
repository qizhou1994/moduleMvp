package com.zq.modulemvp.basemvp.widget.placeholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * desc
 * author zhouqi
 * data 2020/6/3
 */
public class PlaceHolderManager implements IPlaceHolderManager {
    private PlaceHolderLayout layout;
    private PlaceHolderView.Config config;
    private WrapperContext wrapperContext;

    public PlaceHolderManager(@NonNull PlaceHolderView.Config config,
                              @NonNull WrapperContext wrapperContext) {
        this.config = config;
        this.wrapperContext = wrapperContext;
        initPlaceHolderLayout();
    }

    private void initPlaceHolderLayout() {
        Context context = wrapperContext.getContext();
        View oldContent = wrapperContext.getOldContent();
        ViewGroup parent = wrapperContext.getParent();
        layout = new PlaceHolderLayout(context);
        layout.addPlaceHolders(config.getPlaceHolders());
        if (parent != null && oldContent != null) {
            ViewGroup.LayoutParams oldLayoutParams = oldContent.getLayoutParams();
            layout.addView(oldContent, -1, -1);
            if (parent instanceof SmartRefreshLayout) {
                ((SmartRefreshLayout) parent).setRefreshContent(layout);
            } else {
                parent.addView(layout, wrapperContext.getTargetIndex(), oldLayoutParams);
            }
        }
    }

    @Override
    public void showPlaceHolder(@NonNull Class<? extends PlaceHolder> clz) {
        showPlaceHolder(clz, null);
    }

    @Override
    public void showPlaceHolder(@NonNull Class<? extends PlaceHolder> clz,
                                @Nullable IPlaceHolderManager.IExpose expose) {
        layout.showPlaceHolder(clz, expose);
    }

    @Override
    public void hidePlaceHolder() {
        layout.hidePlaceHolder();
    }

    @Override
    public void reset() {
        ViewGroup parent = wrapperContext.getParent();
        View oldContent = wrapperContext.getOldContent();
        if (parent != null) {
            parent.removeView(layout);
            parent.addView(oldContent, wrapperContext.getTargetIndex());
        }
    }
}
