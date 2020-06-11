package com.zq.modulemvp.basemvp.api.fail;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class ResponseException extends Exception {
    private int code;
    private Object data;

    public ResponseException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public ResponseException(String msg, int code, Object obj) {
        super(msg);
        this.code = code;
        this.data = obj;
    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }
}
