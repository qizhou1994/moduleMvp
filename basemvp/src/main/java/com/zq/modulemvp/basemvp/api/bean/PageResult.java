package com.zq.modulemvp.basemvp.api.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class PageResult <T>{
    @SerializedName("list")
    private List<T> data;
    private int totalPages;
    private int totalElements;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
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
