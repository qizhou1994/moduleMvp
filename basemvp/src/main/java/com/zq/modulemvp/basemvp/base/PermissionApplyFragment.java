package com.zq.modulemvp.basemvp.base;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 *  * For permission apply, start the application-setting Activity
 *  * https://blog.csdn.net/donkor_/article/details/79374442
 * desc
 * author zhouqi
 * data 2020/6/3
 */
public class PermissionApplyFragment extends DialogFragment {
    private String mTitle;
    private String mMsg;
    public PermissionApplyFragment(String title, String message) {
        super();
        this.mTitle = title;
        this.mMsg = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mMsg);
        builder.setTitle(mTitle);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            // TODO open settings page
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
        });

        AlertDialog d = builder.create();
        return d;
    }
}
