package com.zq.modulemvp.index.contract;

import com.zq.modulemvp.basemvp.api.bean.Result;
import com.zq.modulemvp.basemvp.base.IBasePresenter;
import com.zq.modulemvp.basemvp.base.IBaseView;

import io.reactivex.Flowable;

/**
 * desc
 * author zhouqi
 * data 2020/6/10
 */
public interface ContractMain {

    interface View extends IBaseView {
        void setNewListView();
    }

    interface Presenter extends IBasePresenter<View> {
        void getNewList(String curVersion);
    }

    interface Model {
        Flowable<Result<String>> getNewList(String curVersion);
    }

}
