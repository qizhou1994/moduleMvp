package com.zq.modulemvp.basemvp.base;

/**
 * desc
 * author zhouqi
 * data 2020/6/2
 */
public interface IBasePresenter<T extends IBaseView> {
    /**
     * bind a view to presenter
     * @param view activity/fragment
     */
    void attachView(T view);

    /**
     * unbind a view from presenter
     */
    void detachView();
}
