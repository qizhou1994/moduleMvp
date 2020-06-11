package com.zq.modulemvp.basemvp;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class DataType {

    /*返回数据为String*/
    public static final int STRING = 1;
    /*返回数据为xml类型*/
    public static final int XML = 2;
    /*返回数据为json对象*/
    public static final int JSON_OBJECT = 3;
    /*返回数据为json数组*/
    public static final int JSON_ARRAY = 4;

    /**
     * 自定义一个播放器状态注解
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STRING, XML, JSON_OBJECT, JSON_ARRAY})
    public @interface Type {
    }
}
