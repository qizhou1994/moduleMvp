package com.zq.modulemvp.basemvp.api.bean;

import com.google.gson.annotations.SerializedName;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class Result<T> {
    public static final int SUCCESS_CODE = 0;

    private int code;
    @SerializedName("msg")
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == SUCCESS_CODE;
    }
}