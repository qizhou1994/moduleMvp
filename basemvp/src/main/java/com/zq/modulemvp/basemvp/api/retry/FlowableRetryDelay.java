package com.zq.modulemvp.basemvp.api.retry;

import androidx.annotation.NonNull;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class FlowableRetryDelay implements Function<Flowable<Throwable>, Publisher<?>> {

    private Function<Throwable, RetryConfig> provider;

    private int retryCount;

    public FlowableRetryDelay(Function<Throwable, RetryConfig> provider) {
        ObjectHelper.requireNonNull(provider, "provider is null");
        this.provider = provider;
    }

    @Override
    public Publisher<?> apply(@NonNull Flowable<Throwable> throwableFlowable) throws Exception {
        return throwableFlowable.flatMap(throwable -> {

            RetryConfig config = provider.apply(throwable);

            if (++retryCount <= config.getMaxRetries()) {
                return config
                        .getRetryCondition()
                        .call()
                        .flatMapPublisher((Function<Boolean, Publisher<?>>) retry -> {
                            if (retry) {
                                return Flowable.timer(config.getDelay(), TimeUnit.MILLISECONDS);
                            } else {
                                return Flowable.error(throwable);
                            }
                        });
            }
            return Flowable.error(throwable);
        });
    }
}
