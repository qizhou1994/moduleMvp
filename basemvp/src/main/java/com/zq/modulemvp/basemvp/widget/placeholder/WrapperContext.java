package com.zq.modulemvp.basemvp.widget.placeholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * desc
 * author zhouqi
 * data 2020/6/3
 */
public class WrapperContext {
    private Context context;
    /**
     * The parent layout of the view that fills the placeholders.
     */
    private ViewGroup parent;
    /**
     * the view will be replaced by place-holder view.
     */
    private View oldContent;
    /**
     * The position of the target view in its parent container.
     */
    private int targetIndex;

    public WrapperContext(@NonNull Context context, @Nullable ViewGroup parent,
                          @Nullable View oldContent, int targetIndex) {
        this.context = context;
        this.parent = parent;
        this.oldContent = oldContent;
        this.targetIndex = targetIndex;
    }

    @NonNull
    public Context getContext() {
        return context;
    }

    @Nullable
    public ViewGroup getParent() {
        return parent;
    }

    @Nullable
    public View getOldContent() {
        return oldContent;
    }

    public int getTargetIndex() {
        return targetIndex;
    }
}
