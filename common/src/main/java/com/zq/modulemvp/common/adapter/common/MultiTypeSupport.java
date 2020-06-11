package com.zq.modulemvp.common.adapter.common;

/**
 * @description
 *
 * @author qizhou
 * @version 1.0
 * @date 2019/12/11
 */
public interface MultiTypeSupport<T> {
    // 根据当前位置或者条目数据返回布局
    public int getLayoutId(T item, int position);
}
