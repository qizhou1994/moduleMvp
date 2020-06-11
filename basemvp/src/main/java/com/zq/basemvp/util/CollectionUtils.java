package com.zq.basemvp.util;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class CollectionUtils {
    public static <T> int size(T... t) {
        return t == null ? 0 : t.length;
    }

    public static boolean isBlank(Collection<?> list) {
        return !isNotBlank(list);
    }

    public static boolean isNotBlank(Collection<?> list) {
        return list != null && list.size() > 0;
    }

    public interface TraversalWeakRefListener<T> {
        /**
         * Traversal all list item
         * @param index index
         * @param item list item object
         * @return true if continue
         */
        boolean onNext(int index, T item);
    }

    public interface TraversalWeakRefListenerWithReduce<T, R> {
        /**
         * Traversal all list item
         * @param index index
         * @param item list item object
         * @param result data holder
         * @return
         */
        boolean onNext(int index, T item, TraversalReduce<R> result);
    }

    public static class TraversalReduce<T> {
        public T data;

        public TraversalReduce(T data) {
            this.data = data;
        }
    }

    public static <T> void traversalWeakRefListAndRemoveEmpty(List<WeakReference<T>> list,
                                                              TraversalWeakRefListener<T> listener) {
        if (isBlank(list)) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null) {
                T item = list.get(i).get();
                if (item != null) {
                    if (listener.onNext(i, item)) {
                        continue;
                    } else {
                        break;
                    }
                }
            }

            list.remove(i);
            i--;
        }
    }

    public static <T, R> void traversalWeakRefListAndRemoveEmpty(List<WeakReference<T>> list,
                                                                 TraversalReduce<R> reduce,
                                                                 TraversalWeakRefListenerWithReduce<T, R> listener) {
        if (isBlank(list)) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null) {
                T item = list.get(i).get();
                if (item != null) {
                    if (listener.onNext(i, item, reduce)) {
                        continue;
                    } else {
                        break;
                    }
                }
            }

            list.remove(i);
            i--;
        }
    }
}
