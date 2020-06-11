package com.zq.modulemvp.basemvp.api.retry;


import com.zq.modulemvp.basemvp.api.func.Supplier;

import io.reactivex.Single;
import io.reactivex.internal.functions.ObjectHelper;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class RetryConfig {

    private static int DEFAULT_RETRY_MAX = 1;
    private static int DEFAULT_DELAY = 1000;
    private static final Supplier<Single<Boolean>> DEFAULT_FUNCTION = () -> Single.just(false);

    private int mMaxRetries;
    private int delay;

    private Supplier<Single<Boolean>> retryCondition;

    public RetryConfig() {
        this(DEFAULT_RETRY_MAX, DEFAULT_DELAY, DEFAULT_FUNCTION);
    }

    public RetryConfig(int maxRetries) {
        this(maxRetries, DEFAULT_DELAY, DEFAULT_FUNCTION);
    }

    public RetryConfig(int maxRetries, int delay) {
        this(maxRetries, delay, DEFAULT_FUNCTION);
    }

    public RetryConfig(Supplier<Single<Boolean>> retryCondition) {
        this(DEFAULT_RETRY_MAX, DEFAULT_DELAY, retryCondition);
    }

    public RetryConfig(int maxRetries, int delay, Supplier<Single<Boolean>> retryCondition) {
        ObjectHelper.requireNonNull(retryCondition, "retryCondition is null");

        this.mMaxRetries = maxRetries;
        this.delay = delay;
        this.retryCondition = retryCondition;
    }

    public int getMaxRetries() {
        return mMaxRetries;
    }

    public int getDelay() {
        return delay;
    }

    public Supplier<Single<Boolean>> getRetryCondition() {
        return retryCondition;
    }
}
