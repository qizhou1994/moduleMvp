package com.zq.modulemvp.basemvp.base;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.zq.modulemvp.basemvp.R;

/**
 * desc
 * author zhouqi
 * data 2020/6/3
 */
public class ToastUtil {
    private ToastUtil() {
    }

    private static Toast mToast;

    public static void showToastShort(@NonNull String text) {
        custom(text, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(@NonNull String text) {
        custom(text, Toast.LENGTH_LONG).show();
    }

    public static Toast custom(@NonNull CharSequence message, int duration) {
        return makeToast(message, duration);
    }

    private static boolean isBefore810() {
        int result = compareVersion("8.1.0", android.os.Build.VERSION.RELEASE);
        return result < 1;
    }

    private static Toast makeToast(@NonNull CharSequence message, int duration) {
        Context context = BaseApplication.getContext();
        if (isBefore810()) {
            mToast = null;
        }
        if (mToast == null) {
            mToast = newToast(context, message, duration);
        } else {
            mToast.setText(message);
            mToast.setDuration(duration);
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        return mToast;
    }

    /**
     * create a custom toast, with black background and white text.
     * @param context context
     * @param text toast message
     * @param duration duration type
     * @return a Toast instance
     */
    private static Toast newToast(Context context, CharSequence text, int duration) {
        Toast result = new Toast(context);
        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflate.inflate(R.layout.toast_layout, null);
        TextView tv = root.findViewById(android.R.id.message);
        tv.setText(text);
        result.setView(root);
        result.setDuration(duration);
        return result;
    }

    /**
     * 这里reset是在UI基类里做的，并不是所有的Activity都继承了UI.但是mToast本身占用内存很小，暂时不处理。
     * 这里的context为全局，不会出现内存泄露情况
     */
    public static void resetToast() {
        if (mToast != null) {
            mToast = null;
        }
    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    /**
     * compare the two version
     * @param version1 v1
     * @param version2 v2
     * @return 1 if version1 grater than version2, -1 if version1 less than version2, otherwise 0.
     */
    public static int compareVersion(String version1, String version2) {

        if (version1.equals(version2)) {
            return 0;
        }
        String[] v1Array = version1.split("\\.");
        String[] v2Array = version2.split("\\.");

        int index = 0;
        int minLen = Math.min(v1Array.length, v2Array.length);
        int diff = 0;
        // compare every digit in a loop
        while (index < minLen &&
                (diff = parseInt(v1Array[index]) - parseInt(v2Array[index])) == 0) {
            index++;
        }

        if (diff == 0) {
            // if length of version1 grater than version2
            for (int i = index; i < v1Array.length; i++) {
                if (parseInt(v1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < v2Array.length; i++) {
                if (parseInt(v2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    private static int parseInt(String code) {
        int ret;
        try {
            ret = Integer.parseInt(code);
        } catch (Exception e) {
            ret = 0;
        }
        return ret;
    }
}
