package com.zq.modulemvp.basemvp.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import io.reactivex.annotations.NonNull;

/**
 * desc
 * author zhouqi
 * data 2020/6/2
 */
public abstract class BaseMvpFragment <T extends IBasePresenter> extends BaseFragment {
    /**
     * bind view to presenter
     * {@code presenter.attachView(this)}
     *
     * @param presenter p
     */
    protected abstract void attachView(T presenter);

    @NonNull
    protected T mPresenter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPresenter = PresenterUtils.getBasePresenter(this.getClass());
        if (mPresenter == null) {
            throw new IllegalArgumentException("MVP fragment must has presenter");
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    protected void init(View view) {
        super.init(view);
        attachView(mPresenter);
    }
}
