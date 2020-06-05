package com.zq.modulemvp.basemvp.base;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.zq.modulemvp.basemvp.R;
import com.zq.modulemvp.basemvp.util.StatusBarUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.AutoSizeConfig;
import me.yokeyword.fragmentation.SwipeBackLayout;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * desc
 * author zhouqi
 * data 2020/6/2
 */
public abstract class BaseActivity extends BaseRxActivity {


    protected Unbinder unbinder;
    protected ExecutorService service;

    private PermissionApplier requesterDelegate;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requesterDelegate = new PermissionApplier(this);
        // Ref: https://blog.csdn.net/starry_eve/article/details/82777160
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        int layout = contentLayout();
        if (layout > 0) {
            setContentView(layout);
            unbinder = ButterKnife.bind(this);
        }
        setStatusBar();
        init();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!disableSwipePermanently()) {
//            attachToSwipeBack();
            //todo 会超栈
//            getSwipeBackLayout().attachToActivity(this);
        }
    }

    private void setStatusBar() {
        int color = getResources().getColor(R.color.window_background);
        StatusBarUtil.setColorForSwipeBack(this, color);
        StatusBarUtil.setDarkMode(this);
    }

    /**
     * To get the content view layoutId
     *
     * @return int rsId
     */
    protected abstract int contentLayout();

    /**
     * Call before the {@code onCreate} finished
     */
    protected void init() {
        initWorkingPool();
        initSwipeBack();
        setAnimation();
    }

    /**
     * init thread pool for activity background work.
     */
    protected void initWorkingPool() {
    }

    /**
     * Call when activity {@link #onDestroy }.
     *
     * @param works background works
     */
    protected void leftWorks(List<Runnable> works) {
    }

    private void initSwipeBack() {
        int edgeSize = getEdgeSize();
        if (edgeSize > 0) {
            setEdgeLevel(edgeSize);
        } else {
            setEdgeLevel(getEdgeLevel());
        }
        setSwipeBackEnable(!disableSwipeBack());
    }

    protected SwipeBackLayout.EdgeLevel getEdgeLevel() {
        return SwipeBackLayout.EdgeLevel.MED;
    }

    protected int getEdgeSize() {
        return -1;
    }

    /**
     * indicate this component can swipe back
     *
     * @return true if you want swipe back
     * @see #disableSwipePermanently()
     */
    protected boolean disableSwipeBack() {
        return false;
    }

    /**
     * Set the current fragment can not swipe-back.
     * <p>
     * If want to disable swipe-back feature temporarily, you should use {@link #disableSwipeBack}.
     *
     * @return true, current fragment can not
     * @see #disableSwipeBack()
     */
    protected boolean disableSwipePermanently() {
        return false;
    }

    /**
     * all fragment animation
     * It's no effect if {@code setFragmentAnimator()} of support-fragment have been set.
     */
    protected void setAnimation() {
        // right enter, and right exit
        // FragmentAnimator fAnim = new FragmentAnimator(R.anim.h_fragment_enter, R.anim.h_fragment_exit);
        FragmentAnimator fAnim = new DefaultHorizontalAnimator();

        /// right enter, left out, Push animation. open if you need.
        // fAnim.setPopEnter(R.anim.h_fragment_pop_enter);
        // fAnim.setPopExit(R.anim.h_fragment_pop_exit);
        setFragmentAnimator(fAnim);
    }

    public Postcard navigate(String path) {
        return navigate(path, getActivityAnim());
    }

    public Postcard navigate(String path, ActivityOptionsCompat custom) {
        Postcard postcard = ARouter.getInstance().build(path);
        if (custom != null) {
            postcard.withOptionsCompat(custom);
        }
        return postcard;
    }

    protected ActivityOptionsCompat getActivityAnim() {
        return ActivityOptionsCompat.makeCustomAnimation(this, R.anim.h_fragment_enter, R.anim.h_fragment_exit);
    }

    protected int onAnimExitIn() {
        return R.anim.no_anim;
    }

    protected int onAnimExitOut() {
        return R.anim.h_fragment_exit;
    }

    @Override
    public Resources getResources() {
        AutoSizeConfig config = AutoSizeConfig.getInstance();
        Resources res = super.getResources();
        // covert density
        int targetDensity = (int) (config.getScreenWidth() * 1.0f / config.getDesignWidthInDp() * 1000);
        int originalDensity = (int) (res.getDisplayMetrics().density * 1000);
        if (originalDensity != targetDensity) {
            AutoSizeCompat.autoConvertDensityOfGlobal(res);
        }
        return res;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (service != null) {
            leftWorks(service.shutdownNow());
        }
        hidePermissionDialog();
    }

    @Override
    public void finish() {
        super.finish();
        // do exit animation, It's different with swipe-back
        overridePendingTransition(onAnimExitIn(), onAnimExitOut());
    }

    /**
     * {@linkplain com.zq.modulemvp.basemvp.base.Constants.Permission}
     *
     * @param group permissions group
     */
    public void applyPermission(@NonNull String[] group, PermissionApplier.PermissionCallback callback) {
        requesterDelegate.applyPermission(group, callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // if current applier is a fragment, just send result to the fragment.
        if (!requesterDelegate.requested) {
            return;
        }
        for (int i = 0; i < grantResults.length; i++) {
            requesterDelegate.notifyPermissionState(grantResults[i], permissions[i]);
        }
    }

    public void showPermissionDialog(String title, String msg) {
        requesterDelegate.showPermissionDialog(title, msg);
    }

    public void hidePermissionDialog() {
        requesterDelegate.hidePermissionDialog();
    }
}
