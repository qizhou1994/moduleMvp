package com.zq.modulemvp.basemvp.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding2.view.RxView;
import com.zq.modulemvp.basemvp.R;
import com.zq.modulemvp.basemvp.util.StatusBarUtil;
import com.zq.modulemvp.basemvp.widget.AppNaviBar;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.SwipeBackLayout;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import me.yokeyword.fragmentation_swipeback.SwipeBackActivity;

/**
 * desc
 * author zhouqi
 * data 2020/6/2
 */
public abstract class BaseFragment extends BaseRxFragment implements IBaseView {

    private View rootView;
    private Unbinder unbinder;
    private PermissionApplier requesterDelegate;
    protected AppNaviBar appNaviBar;

    /**
     * auto dismiss
     */
    protected static final int TIPS_TYPE_TOAST = 0;
    /**
     * progress status
     */
    protected static final int TIPS_TYPE_PROGRESS = 1;
    protected static final int TOAST_DUR_SHORT = 3000;
    protected static final int TOAST_DUR_LONG = 7000;
    private static final int MSG_DISMISS = 0x43;

    protected boolean isEnterAnimEnd;
    protected Handler mMainHandler;
    private Runnable mDlgMissAction;
    private Dialog mTipsDialog;
    private Dialog mProgsDialog;
    public CompositeDisposable mCompositeDisposable;

    private String mTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainHandler = new MainHandler(this);
        setAnimByArgs();
    }

    /**
     * set the fragment animator
     */
    private void setAnimByArgs() {
        Bundle args = getArguments();
        if (args != null) {
            FragmentAnimator animator = args.getParcelable(Config.ARG_FRAG_ANIM);
            if (animator != null) {
                setFragmentAnimator(animator);
            }
            mTitle = args.getString(Config.ARG_FRAG_TITLE, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        requesterDelegate = new PermissionApplier(this);
        rootView = inflater.inflate(contentLayout(), container, false);
        Drawable rootBg = rootView.getBackground();
        unbinder = ButterKnife.bind(this, rootView);
        appNaviBar = rootView.findViewById(R.id.app_nav_bar);
        if (rootBg == null) {
            setWindowBackground();
        }
        if (disableSwipePermanently()) {
            return rootView;
        }
        return attachToSwipeBack(rootView);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setStatusBar();
        init(view);
    }

    protected void updateStatusBar(boolean transparent) {
        final int color = getResources().getColor(R.color.window_background);
        if (transparent) {
            StatusBarUtil.setColorForSwipeBack(_mActivity, color);
        } else {
            StatusBarUtil.setStatusBarColor(_mActivity, color);
        }
    }

    protected void setWindowBackground() {
        int color = getResources().getColor(R.color.window_background);
        setWindowBackground(color);
    }

    public void setWindowBackground(int color) {
        rootView.setBackgroundColor(color);
    }

    public void setStatusBar() {
        int color = getResources().getColor(R.color.window_background_dark);
        setStatusBar(color);
    }

    /**
     * create an empty view for status bar
     * and set the window background color
     */
    public void setStatusBar(int color) {
        ViewGroup root = (ViewGroup) rootView;
        int statusHeight = StatusBarUtil.getStatusBarHeight(_mActivity);
        View statusBar = StatusBarUtil.createStatusBarView(_mActivity, color);
        root.setPadding(0, statusHeight, 0, 0);

        // ensure clipTo flag
        root.setClipToPadding(false);
        root.setClipChildren(false);

        ViewGroup.LayoutParams lp = statusBar.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) lp).topMargin = -statusHeight;
            statusBar.setLayoutParams(lp);
        }
        if (rootView instanceof LinearLayout) {
            root.addView(statusBar, 0);
            return;
        }

        // for swipe bg color, should show content view.
        if (lp instanceof RelativeLayout.LayoutParams) {
            ((RelativeLayout.LayoutParams) lp).addRule(RelativeLayout.ALIGN_PARENT_TOP);
            ((RelativeLayout.LayoutParams) lp).addRule(RelativeLayout.ALIGN_PARENT_START);
            statusBar.setLayoutParams(lp);
        }
        // must add to last child, use zIndex automatically
        root.addView(statusBar);
    }

    /**
     * Activity immersive
     * Note: in fragment, need set the background color for custom StatusBar, to use this code:
     * {@code
     *    // enter immersive
     *    statusBar = View.findViewById(R.id.common_fake_status_bar_view)
     *    statusBar.setBackgroundColor(context.getResource().getColor(R.color.you_full_screen_color));
     *
     *    // exit immersive
     *    statusBar = View.findViewById(R.id.common_fake_status_bar_view)
     *    statusBar.setBackgroundColor(context.getResource().getColor(R.color.window_background));
     * }
     * @param immersive true to enter immersive mode
     */
    public void immersive(boolean immersive) {
        if (!isAdded() || getContext() == null) {
            return;
        }
        Window window = _mActivity.getWindow();
        WindowManager.LayoutParams attr = window.getAttributes();
        if (immersive) {
            attr.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(attr);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        } else {
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(attr);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * provide the content view
     *
     * @return layout res Id
     */
    public abstract int contentLayout();

    @CallSuper
    protected void init(View view) {
        int edgeSize = getEdgeSize();
        if (edgeSize > 0) {
            setEdgeLevel(edgeSize);
        } else {
            setEdgeLevel(getEdgeLevel());
        }
        // call super
        super.setSwipeBackEnable(!disableSwipeBack());
        initNaviBar();
    }

    public View getRootView() {
        return rootView;
    }

    /**
     * Indicate this component can swipe back
     * Note:
     * root fragment must return true manually
     *
     * @return true if you want disable swipe back temporary
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
     * @return true, current fragment can not attach the {@link SwipeBackLayout}
     * @see #disableSwipeBack()
     */
    protected boolean disableSwipePermanently() {
        return false;
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        super.setSwipeBackEnable(enable);
        if (_mActivity != null && _mActivity instanceof SwipeBackActivity) {
            ((SwipeBackActivity) _mActivity).setSwipeBackEnable(enable);
        }
    }

    protected SwipeBackLayout.EdgeLevel getEdgeLevel() {
        return SwipeBackLayout.EdgeLevel.MED;
    }

    protected int getEdgeSize() {
        return -1;
    }

    protected String getTitle() {
        return mTitle;
    }

    private void initNaviBar() {
        if (appNaviBar == null) {
            return;
        }
        View back = appNaviBar.getLeftIcon();
        if (back != null && back.getVisibility() == View.VISIBLE) {
            back.setOnClickListener(v -> {
                finish();
            });
        }
        final String title = getTitle();
        if (!TextUtils.isEmpty(title)) {
            setTitle(getTitle());
        }
    }

    public void setTitle(String title) {
        if (appNaviBar != null) {
            appNaviBar.setTitle(title);
        }
    }

    public Fragment getRootFragment() {
        Fragment root = this;
        while (root.getParentFragment() != null) {
            root = root.getParentFragment();
        }

        return root;
    }

    /**
     * the {@param toFragment} will show out of bottom NavigationButton
     *
     * @param toFragment who
     */
    public void startWithRoot(ISupportFragment toFragment) {
        Fragment root = getRootFragment();
        if (root != this && root instanceof SupportFragment) {
            ((SupportFragment) root).start(toFragment);
        } else {
            start(toFragment);
        }
    }

    /**
     * {@linkplain  com.zq.modulemvp.basemvp.base.Constants.Permission}
     *
     * @param group permissions group
     */
    public void applyPermission(@NonNull String[] group, PermissionApplier.PermissionCallback callback) {
        requesterDelegate.applyPermission(group, callback);
    }

    public void showPermissionDialog(String title, String msg) {
        requesterDelegate.showPermissionDialog(title, msg);
    }

    public void hidePermissionDialog() {
        if (requesterDelegate != null) {
            requesterDelegate.hidePermissionDialog();
        }
    }

    /**
     * finish self as the activity does
     */
    public void finish() {
        _mActivity.onBackPressedSupport();
    }

    /**
     * ddd
     * @param v view
     * @param action working
     * @return disposable ignore
     */
    public void throttleClick(View v, final Action action) {
        addDisposable(RxView.clicks(v)
                .throttleLast(Config.VIEW_THROTTLE_TIME, Config.VIEW_THROTTLE_UNIT)
                .subscribe(unit -> action.run()));
    }

    public void throttleClickMain(View v, final Action action) {
        addDisposable(RxView.clicks(v)
                .throttleLast(Config.VIEW_THROTTLE_TIME, Config.VIEW_THROTTLE_UNIT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> action.run()));
    }

    protected void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    protected void removeDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    protected void navigateFragment(Postcard postcard) {
        if (!isAdded() || _mActivity == null) {
            return;
        }
        Object ret = postcard.navigation(_mActivity);
        if (ret instanceof ISupportFragment) {
            startWithRoot((ISupportFragment) ret);
        }
    }

    protected Postcard navigate(String path) {
        ActivityOptionsCompat optionsCompat = null;
        if (_mActivity instanceof BaseActivity) {
            optionsCompat = ((BaseActivity) _mActivity).getActivityAnim();
        }
        return navigate(path, optionsCompat);
    }

    /**
     * @param path route path
     * @param custom switch animation. You can use
     * {@link ActivityOptionsCompat#makeCustomAnimation(Context, int, int)} to generate one
     * @return a post card {@linkplain Postcard}
     */
    protected Postcard navigate(String path, ActivityOptionsCompat custom) {
        if (!ensureActivity()) {
            return null;
        }
        if (_mActivity instanceof BaseActivity) {
            return ((BaseActivity) _mActivity).navigate(path, custom);
        }
        Postcard postcard = ARouter.getInstance().build(path);
        if (custom != null) {
            postcard.withOptionsCompat(custom);
        }
        return postcard;
    }

    private boolean ensureActivity() {
        return isAdded() && _mActivity != null;
    }

    public void toast(String message) {
        ToastUtil.showToastShort(message);
    }

    public void toastLong(String message) {
        ToastUtil.showToastLong(message);
    }

    public void toast(String message, int icon) {
        toast(message, icon, TIPS_TYPE_TOAST, TOAST_DUR_SHORT);
    }

    public void toastLong(String message, int icon) {
        toast(message, icon, TIPS_TYPE_TOAST, TOAST_DUR_LONG);
    }

    public void toast(String message, int icon, int type, int dur) {
        if (type == TIPS_TYPE_TOAST && shouldSkip()) {
            ToastUtil.cancel();
            return;
        }
        showTips(message, icon, type, dur);
    }

    public void toastProgress(String message, int icon, int dur){
        toast(message, icon, TIPS_TYPE_PROGRESS, 10000);
    }

    public void toastLoading(String message, int dur) {
        toast(message, 0, TIPS_TYPE_PROGRESS, dur);
    }

    public void toast(String message, int icon, int type) {
        showTips(message, icon, type, TOAST_DUR_SHORT);
    }

    public void dismissDialog() {
        if (mDlgMissAction != null) {
            mDlgMissAction.run();
        }
    }

    private boolean shouldSkip() {
        return mTipsDialog != null && mTipsDialog.isShowing();
    }

    private void showTips(CharSequence tips, int iconId, int type, int duration) {
        if (mMainHandler == null || mMainHandler.hasMessages(MSG_DISMISS)) {
            return;
        }
        if (mDlgMissAction == null) {
            mDlgMissAction = () -> {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    return;
                }
                if (mTipsDialog != null && mTipsDialog.isShowing()) {
                    mTipsDialog.dismiss();
                }
                if (mProgsDialog != null && mProgsDialog.isShowing()) {
                    mProgsDialog.dismiss();
                }
                mMainHandler.removeMessages(MSG_DISMISS);
            };
        }
        Window dlgWindow;
        if (type == TIPS_TYPE_TOAST) {
            showToastTips(tips, iconId, duration);
            dlgWindow = mTipsDialog.getWindow();
        } else {
            showProgressTips(tips, iconId, duration);
            dlgWindow = mProgsDialog.getWindow();
        }
        if (dlgWindow != null) {
            setWindowStatus(dlgWindow, type == TIPS_TYPE_TOAST);
        }
    }

    private void showToastTips(CharSequence tips, int iconId, int duration) {
        initToastContent(tips, iconId);
        mMainHandler.sendMessageDelayed(
                mMainHandler.obtainMessage(MSG_DISMISS, mDlgMissAction), duration);
        mTipsDialog.show();
    }

    private void showProgressTips(CharSequence tips, int iconId, int duration) {
        initProgressContent(tips, iconId);
        mProgsDialog.setCancelable(false);
        mProgsDialog.setCanceledOnTouchOutside(false);
        mProgsDialog.show();
        if (duration > 0) {
            mMainHandler.sendMessageDelayed(
                    mMainHandler.obtainMessage(MSG_DISMISS, mDlgMissAction), duration);
        }
    }

    private void initToastContent(CharSequence tips, int iconId) {
        ImageView icon;
        TextView title;
        if (mTipsDialog == null) {
            mTipsDialog = inflateDialog(R.style.ModeuleMvpDialog);
        } else {
            mDlgMissAction.run();
        }
        icon = mTipsDialog.findViewById(R.id.iv_toast_tips);
        title = mTipsDialog.findViewById(android.R.id.message);
        title.setText(tips);
        if (iconId > 0) {
            icon.setImageResource(iconId);
        }
    }

    private void initProgressContent(CharSequence tips, int iconId) {
        ImageView icon;
        ProgressBar progressBar;
        TextView title;
        if (mProgsDialog == null) {
            mProgsDialog = inflateDialog(R.style.ModeuleMvpDialog);
        } else {
            mDlgMissAction.run();
        }
        icon = mProgsDialog.findViewById(R.id.iv_toast_tips);
        progressBar = mProgsDialog.findViewById(R.id.progress_bar);
        title = mProgsDialog.findViewById(android.R.id.message);
        title.setText(tips);
        if (iconId > 0) {
            icon.setImageResource(iconId);
            progressBar.setVisibility(View.GONE);
            centerIconIfNeed(tips, title, icon);
        } else {
            icon.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            centerIconIfNeed(tips, title, progressBar);
        }
    }

    private void centerIconIfNeed(CharSequence tips, View title, View icon) {
        if (TextUtils.isEmpty(tips)) {
            title.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) icon.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            icon.setLayoutParams(lp);
        }
    }

    private Dialog inflateDialog(int themeId) {
        Dialog dialog = new Dialog(requireActivity(), themeId);
        View root = getLayoutInflater().inflate(R.layout.toast_icon_layout, null);
        dialog.setContentView(root);
        return dialog;
    }

    private void setWindowStatus(Window window, boolean toast) {
        final WindowManager.LayoutParams params = window.getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        if (toast) {
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }

        // tips
        View root;
        root = window.getDecorView();
        root.measure(0, 0);
        int w = root.getMeasuredWidth();
        int h = root.getMeasuredHeight();
        int size = Math.max(w, h);
        params.width = size;
        params.height = size;
        window.setAttributes(params);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            requesterDelegate.notifyPermissionState(grantResults[i], permissions[i]);
        }
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        isEnterAnimEnd = true;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        for (Fragment f : getChildFragmentManager ().getFragments()) {
            if (f instanceof SupportFragment) {
                ((SupportFragment) f).onFragmentResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onDestroyView() {
        hidePermissionDialog();
        super.onDestroyView();
        hideSoftInput();
    }

    @Override
    public void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        if (mDlgMissAction != null) {
            mDlgMissAction.run();
        }
        if (mMainHandler != null) {
            mMainHandler.removeMessages(MSG_DISMISS);
        }
        mDlgMissAction = null;
        mMainHandler = null;
        removeDisposable();
        super.onDestroy();
    }

    protected void exitWithPath(Object naviRet) {
        if (naviRet instanceof ISupportFragment) {
            startWithPop((ISupportFragment) naviRet);
        } else {
            // exit when animation end
            UiHandler.postDelayed(() -> exit(), Config.TRANSACTION_DURATION + 150);
        }
    }

    private void exit() {
        FragmentAnimator f = getFragmentAnimator();
        f.setExit(R.anim.no_anim);
        f.setPopEnter(R.anim.no_anim);
        finish();
    }

    protected void disableHorAnim() {
        FragmentAnimator anim = getFragmentAnimator();
        anim.setExit(R.anim.no_anim);
        anim.setPopEnter(R.anim.no_anim);
        setFragmentAnimator(anim);
    }

    protected void restoreHorAnim() {
        FragmentAnimator anim = getFragmentAnimator();
        anim.setExit(R.anim.h_fragment_exit);
        anim.setPopEnter(R.anim.h_fragment_pop_enter);
        setFragmentAnimator(anim);
    }

    private static class MainHandler extends Handler {
        private WeakReference<BaseFragment> fragmentRef;
        public MainHandler(BaseFragment fragment) {
            fragmentRef = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (fragmentRef == null || fragmentRef.get() == null) {
                return;
            }
            if (msg.what == MSG_DISMISS) {
                ((Runnable) msg.obj).run();
            }
        }
    }
}

