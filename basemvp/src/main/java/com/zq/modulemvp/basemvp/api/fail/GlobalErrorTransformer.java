package com.zq.modulemvp.basemvp.api.fail;

import com.zq.modulemvp.basemvp.api.func.Supplier;
import com.zq.modulemvp.basemvp.api.retry.FlowableRetryDelay;
import com.zq.modulemvp.basemvp.api.retry.ObservableRetryDelay;
import com.zq.modulemvp.basemvp.api.retry.RetryConfig;

import org.reactivestreams.Publisher;


import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class GlobalErrorTransformer<T> implements ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T>,
        CompletableTransformer {

    private Supplier<Scheduler> upStreamSchedulerProvider;
    private Supplier<Scheduler> downStreamSchedulerProvider;

    private Function<T, Observable<T>> globalOnNextRetryInterceptor;
    private Function<Throwable, Observable<T>> globalOnErrorResume;
    private Function<Throwable, Observable<T>> emptyOnErrorResume;
    private Function<Throwable, RetryConfig> retryConfigProvider;
    private Consumer<Throwable> globalDoOnErrorConsumer;

    public GlobalErrorTransformer(Function<T, Observable<T>> globalOnNextRetryInterceptor,
                                  Function<Throwable, Observable<T>> globalOnErrorResume,
                                  Function<Throwable, RetryConfig> retryConfigProvider,
                                  Consumer<Throwable> globalDoOnErrorConsumer) {
        this(
                AndroidSchedulers::mainThread,
                AndroidSchedulers::mainThread,
                globalOnNextRetryInterceptor,
                globalOnErrorResume,
                retryConfigProvider,
                globalDoOnErrorConsumer
        );
    }

    public GlobalErrorTransformer(Supplier<Scheduler> upStreamSchedulerProvider,
                                  Supplier<Scheduler> downStreamSchedulerProvider,
                                  Function<T, Observable<T>> globalOnNextRetryInterceptor,
                                  Function<Throwable, Observable<T>> globalOnErrorResume,
                                  Function<Throwable, RetryConfig> retryConfigProvider,
                                  Consumer<Throwable> globalDoOnErrorConsumer) {
        this.upStreamSchedulerProvider = upStreamSchedulerProvider;
        this.downStreamSchedulerProvider = downStreamSchedulerProvider;
        this.globalOnNextRetryInterceptor = globalOnNextRetryInterceptor;
        this.globalOnErrorResume = globalOnErrorResume;
        this.retryConfigProvider = retryConfigProvider;
        this.globalDoOnErrorConsumer = globalDoOnErrorConsumer;
        this.emptyOnErrorResume = new Function<Throwable, Observable<T>>() {
            @Override
            public Observable<T> apply(Throwable t) throws Exception {
                if (t instanceof NullPointerException) {
                    // T a = new T();
                    return Observable.empty();
                }
                return Observable.error(t);
            }
        };
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream
                .observeOn(upStreamSchedulerProvider.call())
                .flatMap((Function<T, ObservableSource<T>>) t -> {
                    return globalOnNextRetryInterceptor.apply(t);
                })
                .onErrorResumeNext(throwable -> {
                    return globalOnErrorResume.apply(throwable);
                })
                .retryWhen(new ObservableRetryDelay(retryConfigProvider))
                .doOnError(globalDoOnErrorConsumer)
                .observeOn(downStreamSchedulerProvider.call());
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return upstream
                .observeOn(upStreamSchedulerProvider.call())
                .onErrorResumeNext(throwable -> {
                    return globalOnErrorResume
                            .apply(throwable)
                            .ignoreElements();
                })
                .retryWhen(new FlowableRetryDelay(retryConfigProvider))
                .doOnError(globalDoOnErrorConsumer)
                .observeOn(downStreamSchedulerProvider.call());
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream
                .observeOn(upStreamSchedulerProvider.call())
                .flatMap((Function<T, Publisher<T>>) t -> {
                    return globalOnNextRetryInterceptor
                            .apply(t)
                            .toFlowable(BackpressureStrategy.BUFFER);
                })
                .onErrorResumeNext((Function<Throwable, Publisher<T>>) t -> {
                    return emptyOnErrorResume
                            .apply(t)
                            .toFlowable(BackpressureStrategy.BUFFER);
                })
                .onErrorResumeNext((Function<Throwable, Publisher<T>>) throwable -> {
                    return globalOnErrorResume
                            .apply(throwable)
                            .toFlowable(BackpressureStrategy.BUFFER);
                })
                .retryWhen(new FlowableRetryDelay(retryConfigProvider))
                .doOnError(globalDoOnErrorConsumer)
                .observeOn(downStreamSchedulerProvider.call());
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream
                .observeOn(upStreamSchedulerProvider.call())
                .flatMap((Function<T, MaybeSource<T>>) t -> {
                    return globalOnNextRetryInterceptor.apply(t).firstElement();
                })
                .onErrorResumeNext((Function<Throwable, MaybeSource<T>>) throwable -> {
                    return globalOnErrorResume.apply(throwable)
                            .firstElement();
                })
                .retryWhen(new FlowableRetryDelay(retryConfigProvider))
                .doOnError(globalDoOnErrorConsumer)
                .observeOn(downStreamSchedulerProvider.call());
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream
                .observeOn(upStreamSchedulerProvider.call())
                .flatMap((Function<T, SingleSource<T>>) t -> {
                    return globalOnNextRetryInterceptor
                            .apply(t)
                            .firstOrError();
                })
                .onErrorResumeNext((Function<Throwable, SingleSource<T>>) throwable -> {
                    return globalOnErrorResume
                            .apply(throwable)
                            .firstOrError();
                })
                .retryWhen(new FlowableRetryDelay(retryConfigProvider))
                .doOnError(globalDoOnErrorConsumer)
                .observeOn(downStreamSchedulerProvider.call());
    }
}