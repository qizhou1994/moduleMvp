package com.zq.modulemvp.basemvp.base;

import com.zq.modulemvp.basemvp.api.fail.GlobalErrorUtil;
import com.zq.modulemvp.basemvp.util.RxUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class RxPresenter<T extends IBaseView> implements IBasePresenter<T> {
    protected String TAG = this.getClass().getSimpleName();

    private WeakReference<T> mViewReference;

    protected T mView;

    @Override
    public void attachView(T view) {
        this.mViewReference = new WeakReference<>(view);
        // like the null check, if (mView != null) {}
        Class<?> clz = view.getClass();
        mView = (T) Proxy.newProxyInstance(clz.getClassLoader(), clz.getInterfaces(),
                (proxy, method, args) -> {
                    if (mViewReference == null || mViewReference.get() == null) {
                        return null;
                    } else {
                        return method.invoke(mViewReference.get(), args);
                    }
                });
        registerEvent();
    }

    protected void registerEvent() {
    }

    @Override
    public void detachView() {
        this.mViewReference = null;
    }

    protected <T> Flowable<T> observeOnIO(Flowable<T> observable) {
        return observable
                .compose(GlobalErrorUtil.handleGlobalError())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());
    }

    protected <T> Flowable<T> observeMain(Flowable<T> observable) {
        return observable
                .compose(GlobalErrorUtil.handleGlobalError())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());
    }

    protected <T> Flowable<T> observe(Flowable<T> observable) {
        return observable
                .compose(GlobalErrorUtil.handleGlobalError())
                .compose(RxUtils.rxSchedulerHelperForFlowable());
    }
}
