package com.zq.modulemvp.basemvp.base;

import androidx.annotation.CallSuper;

import io.reactivex.annotations.NonNull;

/**
 * desc
 * author zhouqi
 * data 2020/6/2
 */
public abstract class BaseMvpActivity<T extends IBasePresenter> extends BaseActivity implements IBaseView {

    /**
     * bind view to presenter
     *
     * @param presenter p
     */
    protected abstract void attachView(T presenter);

    @NonNull
    protected T mPresenter;

    public BaseMvpActivity() {
        mPresenter = PresenterUtils.getBasePresenter(this.getClass());
        if (mPresenter == null) {
            throw new IllegalArgumentException("MVP activity must has presenter");
        }
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    protected void init() {
        super.init();
        attachView(mPresenter);
    }
}