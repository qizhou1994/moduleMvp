package com.zq.modulemvp.basemvp.api.fail;


import com.zq.modulemvp.basemvp.api.bean.Result;
import com.zq.modulemvp.basemvp.api.retry.RetryConfig;
import com.zq.modulemvp.basemvp.base.Config;
import com.zq.modulemvp.basemvp.util.AppLog;

import java.net.ConnectException;

import io.reactivex.Observable;
import io.reactivex.Single;


/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class GlobalErrorUtil {

    public static <T> GlobalErrorTransformer<T> handleGlobalError() {
        return new GlobalErrorTransformer<T> (
                (next) -> {
                    if (next instanceof Result) {
                        Result result = (Result) next;
                        if (result.getCode() != Config.API_SUCCEED_CODE) {
                            return Observable.error(
                                    new ResponseException(result.getMessage(), result.getCode(), result.getData())
                            );
                        }
                    }
                    return Observable.just(next);
                },
                (error) -> {
                    if (error instanceof ConnectException) {
                        return Observable.error(new ConnectFailedException());
                    }
                    return Observable.error(error);
                },
                (retry) -> {
                    if (retry instanceof ConnectFailedException) {
                        return new RetryConfig(() -> Single.just(true));
                    }
                    return new RetryConfig(0);
                },
                (throwable) -> {
                    AppLog.e("Exception:" + throwable.toString());
                }
        );
    }
}