package com.zq.modulemvp.basemvp.api.bean;

import com.google.gson.annotations.SerializedName;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class PageCurrentResult <T> {
    @SerializedName("list")
    private T data;
    private int totalPages;
    private int totalElements;
    private int currentPages;

    public void setCurrentPages(int currentPages) {
        this.currentPages = currentPages;
    }

    public int getCurrentPages() {
        return currentPages;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }
}
