package com.zq.modulemvp.index.contract;

import com.zq.modulemvp.basemvp.api.bean.Result;
import com.zq.modulemvp.basemvp.base.IBasePresenter;
import com.zq.modulemvp.basemvp.base.IBaseView;
import com.zq.modulemvp.index.bean.RspNewsListBean;

import io.reactivex.Flowable;

/**
 * desc
 * author zhouqi
 * data 2020/6/10
 */
public interface ContractHome {

    interface View extends IBaseView {
        void setNewListView(RspNewsListBean rspNewsListBean);
    }

    interface Presenter extends IBasePresenter<View> {
        void getNewList(String type);
    }

    interface Model {
        Flowable<RspNewsListBean> getNewList(String type);
    }

}
