package com.zq.modulemvp.basemvp.api.http;

/**
 * desc
 * author zhouqi
 * data 2020/6/8
 */
public class BaseResponse<T> {
    private int code;
    private String msg;
    private T model;

    public T getData() {
        return model;
    }

    public void setData(T model) {
        this.model = model;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;

    }

    public boolean isSuccess(){
        return code == 0;
    }

}
