package com.zq.modulemvp.basemvp.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * desc
 * author zhouqi
 * data 2020/6/3
 */
public class PresenterUtils {
    public static boolean isParameterizedType(Class cls) {
        if (cls == null) return false;
        Type type = cls.getGenericSuperclass();
        return type != null && ParameterizedType.class.isAssignableFrom(type.getClass());
    }

    public static <T> T getBasePresenter(Class cls) {
        if (isParameterizedType(cls)) {
            try {
                Type[] types = ((ParameterizedType) cls.getGenericSuperclass()).getActualTypeArguments();
                if (types != null && types.length > 0) {
                    Class<T> clazz = (Class<T>) ReflectUtils.getClass(types[0]);
                    if (clazz == null) {
                        return null;
                    }
                    if (IBasePresenter.class.isAssignableFrom(clazz)) {
                        return clazz.newInstance();
                    } else {
                        throw new Exception("must be with a presenter");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            if (cls == null || !IBaseView.class.isAssignableFrom(cls)) {
                return null;
            }
            return getBasePresenter(cls.getSuperclass());
        }
        return null;
    }
}
