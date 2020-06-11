package com.zq.modulemvp.basemvp.api.retry;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class ObservableRetryDelay implements Function<Observable<Throwable>, ObservableSource<?>> {

    private Function<Throwable, RetryConfig> provider;

    private int retryCount;

    public ObservableRetryDelay(Function<Throwable, RetryConfig> provider) {
        ObjectHelper.requireNonNull(provider, "provider is null");
        this.provider = provider;
    }

    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(throwable -> {

            RetryConfig retryConfig = provider.apply(throwable);

            if (++retryCount <= retryConfig.getMaxRetries()) {
                return retryConfig
                        .getRetryCondition()
                        .call()
                        .flatMapObservable((Function<Boolean, ObservableSource<?>>) retry -> {
                            if (retry)
                                return Observable.timer(retryConfig.getDelay(), TimeUnit.MILLISECONDS);
                            else
                                return Observable.error(throwable);
                        });
            }
            return Observable.error(throwable);
        });
    }
}
