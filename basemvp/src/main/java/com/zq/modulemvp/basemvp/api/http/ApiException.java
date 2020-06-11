package com.zq.modulemvp.basemvp.api.http;

/**
 * desc
 * author zhouqi
 * data 2020/6/8
 */
public class ApiException extends Exception{



    public ApiException(int code, String msg) {

    }

    public String getMsg() {
        return super.getMessage();
    }
}
