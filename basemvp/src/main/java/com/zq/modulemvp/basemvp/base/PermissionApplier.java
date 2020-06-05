package com.zq.modulemvp.basemvp.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tencent.mmkv.MMKV;
import com.zq.modulemvp.basemvp.util.AppLog;

/**
 * desc
 * author zhouqi
 * data 2020/6/2
 */
public class PermissionApplier {
    private static final int DYNAMIC_PERMISSION = 23;
    public static final String PERMISSION_TAG = "permission";

    /**
     * indicate request session, to prevent base-activity {@code onPermissionResult}
     */
    boolean requested = false;
    private BaseActivity mActivity;
    private BaseFragment mFragment;
    private PermissionCallback mCallback;

    public PermissionApplier(BaseActivity activity) {
        this.mActivity = activity;
    }

    public PermissionApplier(BaseFragment fragment) {
        this.mFragment = fragment;
    }

    public void setPermissionCallback(PermissionCallback callback) {
        mCallback = callback;
    }

    /**
     * @author qizhou
     * @description PermissionCallback
     * @since 2019/11/27
     */
    public interface PermissionCallback {
        /**
         * 授权
         */
        void onGranted();

        /**
         * permission denied
         *
         * @see PermissionApplyFragment
         * @param permission which permission
         * @param showReason if true, you need show custom-dialog. settings-applications-permission
         * {@link #showPermissionDialog(String, String)}
         */
        void onDenied(String permission, boolean showReason);

        /**
         * the request dialog has been disabled by user
         *
         * @param permission which permission
         */
        void onDisabled(String permission);
    }

    /**
     * 应用权限
     * {@linkplain com.zq.modulemvp.basemvp.base.Constants.Permission}
     *
     * @param group permissions group
     */
    public void applyPermission(@NonNull String[] group, PermissionCallback callback) {
        if (group.length < 1) {
            return;
        }
        Activity activity;
        if ((activity = getActivity()) == null) {
            AppLog.e("Activity not fund, maybe finished");
            return;
        }
        setPermissionCallback(callback);
        if (Build.VERSION.SDK_INT < DYNAMIC_PERMISSION) {
            notifyPermissionState(PackageManager.PERMISSION_GRANTED, group[0]);
            return;
        }
        boolean hasPermission = true;
        String disabledPermission = null;
        boolean firstRequest = false;
        for (String permission : group) {
            int state = ContextCompat.checkSelfPermission(activity, permission);
            if (state != PackageManager.PERMISSION_GRANTED) {
                // show 'do not ask again' check box
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    // disabled by security manager or first time
                    disabledPermission = permission;
                    int v = MMKV.defaultMMKV().getInt(permission, -1);
                    if (v == -1) {
                        MMKV.defaultMMKV().putInt(permission, 1).apply();
                        firstRequest = true;
                    }
                }
                hasPermission = false;
                break;
            }
        }
        if (!TextUtils.isEmpty(disabledPermission) || firstRequest) {
            if (firstRequest) {
                request(activity, group);
            } else {
                notifyPermissionState(PackageManager.PERMISSION_DENIED, disabledPermission, true);
            }
        } else if (!hasPermission) {
            request(activity, group);
        } else {
            // process the first permission of group, default.
            notifyPermissionState(PackageManager.PERMISSION_GRANTED, group[0]);
        }
    }

    public void notifyPermissionState(int state, String permission) {
        notifyPermissionState(state, permission, false);
    }

    public void notifyPermissionState(int state, String permission, boolean showReason) {
        requested = false;
        if (state == PackageManager.PERMISSION_GRANTED) {
            if (mCallback != null) {
                mCallback.onGranted();
            }
        } else if (state == PackageManager.PERMISSION_DENIED) {
            if (mCallback != null) {
                mCallback.onDenied(permission, showReason);
            }
        } else {
            if (mCallback != null) {
                mCallback.onDisabled(permission);
            }
        }
    }

    public void showPermissionDialog(String title, String msg) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(PERMISSION_TAG);
        if (fragment == null) {
            DialogFragment df = new PermissionApplyFragment(title, msg);
            // TODO cancelable dialog or not.
            df.setShowsDialog(true);
            df.setCancelable(true);
            df.onCancel(new PermissionDialogInterface());
            ft.add(df, PERMISSION_TAG);
            ft.commit();
        } else {
            ft.show(fragment);
        }
    }

    public void hidePermissionDialog() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(PERMISSION_TAG);
        if (fragment != null) {
            ft.remove(fragment);
            ft.commit();
        }
    }

    private Activity getActivity() {
        if (mActivity == null && mFragment == null) {
            throw new NullPointerException("Must support a Activity or Fragment");
        }
        Activity activity = mActivity;
        if (activity == null) {
            if ((activity = mFragment.getActivity()) == null) {
                AppLog.e("activity maybe finished");
            }
        }
        return activity;
    }

    private FragmentManager getFragmentManager() {
        FragmentManager fm;
        if (mActivity != null) {
            fm = mActivity.getSupportFragmentManager();
        } else if (mFragment != null) {
            fm = mFragment.getChildFragmentManager();
        } else {
            throw new NullPointerException("Must support a Activity or Fragment");
        }
        return fm;
    }

    /**
     * request core
     * @param activity host
     * @param group permission group
     */
    private void request(Activity activity, @NonNull String[] group) {
        int requestCode = Constants.Permission.HOLDER.indexOfValue(group);
        if (mFragment != null) {
            mFragment.requestPermissions(group, requestCode);
        } else {
            ActivityCompat.requestPermissions(activity, group, requestCode);
        }
        requested = true;
    }

    private class PermissionDialogInterface implements DialogInterface {
        @Override
        public void cancel() {
            hidePermissionDialog();
        }

        @Override
        public void dismiss() {
            hidePermissionDialog();
        }
    }
}
