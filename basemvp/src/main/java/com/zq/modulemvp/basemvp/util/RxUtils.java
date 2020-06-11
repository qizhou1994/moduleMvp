package com.zq.modulemvp.basemvp.util;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.zq.modulemvp.basemvp.base.BaseApplication;
import com.zq.modulemvp.basemvp.base.BaseRxActivity;
import com.zq.modulemvp.basemvp.base.IBaseView;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * desc
 * author zhouqi
 * data 2020/6/8
 */
public class RxUtils {
    private RxUtils() {}
    public static <T> LifecycleTransformer<T> bindToLifecycle(Object view) {
        if (view == null) {
            throw new NullPointerException("BaseView is null");
        }
        if (view instanceof BaseRxActivity) {
            return ((BaseRxActivity) view).bindToLifecycle();
        } else if (view instanceof RxFragment) {
            return ((RxFragment) view).bindToLifecycle();
        } else {
            throw new IllegalArgumentException("view isn't activity or fragment");
        }
    }

    public static Context getContext(@NonNull IBaseView view) {
        if (view instanceof BaseRxActivity) {
            return (BaseRxActivity) view;
        } else if (view instanceof RxFragment) {
            return ((RxFragment) view).getActivity();
        } else {
            return AppUtil.getContext();
        }
    }

    /**
     * 适用于计算性工作
     * compose 简化线程
     */
    public static <T> ObservableTransformer<T, T> rxComputationSchedulerHelper() {
        return upstream -> upstream.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> rxComputationSchedulerHelperForFlowable() {
        return upstream -> upstream.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 适用于io操作
     * 统一线程处理,在子线程处理，在主线程接收
     * compose 简化线程
     */
    public static <T> ObservableTransformer<T, T> rxSchedulerHelper() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> rxSchedulerHelperForFlowable() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Integer> countdown(int time, TimeUnit unit) {
        if (time < 0) {
            time = 0;
        }
        final int countTime = time;
        return Observable.interval(0, 1, unit)
                .observeOn(AndroidSchedulers.mainThread())
                .map(increaseTime -> countTime - increaseTime.intValue())
                .take(countTime + 1);
    }

    public static Observable<Integer> countdown(int time) {
        return countdown(time, TimeUnit.SECONDS);
    }

    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static <T> void async(final Object view, final Work<T> work, final Main<T> main) {
        Observable.create((ObservableOnSubscribe<T>) e -> {
            T data = work.get();
            if (data == null) {
                e.onComplete();
                return;
            }
            e.onNext(data);
            e.onComplete();
        })
                .compose(RxUtils.bindToLifecycle(view))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(main::doOnMain, e -> AppLog.e("", e));
    }

    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static <T> void async(final Work<T> work, final Main<T> main) {
        Observable.create((ObservableOnSubscribe<T>) e -> {
            T data = work.get();
            if (data == null) {
                e.onComplete();
                return;
            }
            e.onNext(data);
            e.onComplete();
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(main::doOnMain, e -> AppLog.e("", e));
    }

    public interface Work<T> {
        T get();
    }

    public interface Main<T> {
        void doOnMain(T t) throws FileNotFoundException;
    }
}
