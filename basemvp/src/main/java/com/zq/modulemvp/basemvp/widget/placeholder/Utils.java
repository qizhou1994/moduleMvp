package com.zq.modulemvp.basemvp.widget.placeholder;

import java.util.Map;

/**
 * desc
 * author zhouqi
 * data 2020/6/3
 */
public class Utils {
    private Utils() {
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
