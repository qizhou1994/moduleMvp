package com.zq.modulemvp.basemvp.widget.placeholder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

/**
 * desc
 * author zhouqi
 * data 2020/6/3
 */
public class WrapperContextTransformer {
    @NonNull
    public WrapperContext build(@NonNull Activity activity) {
        ViewGroup contentParent = (ViewGroup) activity.findViewById(android.R.id.content);
        View oldContent = null;
        if (contentParent != null) {
            oldContent = contentParent.getChildAt(0);
            contentParent.removeView(oldContent);
        }
        return new WrapperContext(activity, contentParent, oldContent, 0);
    }

    @NonNull
    public WrapperContext build(@NonNull View target) {
        Context context = target.getContext();
        ViewGroup targetParent = (ViewGroup) target.getParent();
        int childIndex = 0;
        int childCount = 0;
        if (targetParent != null) {
            childCount = targetParent.getChildCount();
        }
        for (int i = 0; i < childCount; i++) {
            if (targetParent.getChildAt(i) == target) {
                childIndex = i;
                break;
            }
        }
        if (targetParent != null) {
            targetParent.removeView(target);
        }
        return new WrapperContext(context, targetParent, target, childIndex);
    }
}
