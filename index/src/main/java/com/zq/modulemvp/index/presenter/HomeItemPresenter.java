package com.zq.modulemvp.index.presenter;

import com.zq.modulemvp.basemvp.api.ApiProxy;
import com.zq.modulemvp.basemvp.api.NormalSubscriber;
import com.zq.modulemvp.basemvp.base.RxPresenter;
import com.zq.modulemvp.index.api.IIndexApi;
import com.zq.modulemvp.index.bean.RspNewsListBean;
import com.zq.modulemvp.index.contract.ContractHomeItem;
import com.zq.modulemvp.index.contract.ContractHomeItem;

import io.reactivex.Flowable;

/**
 * desc
 * author zhouqi
 * data 2020/6/11
 */
public class HomeItemPresenter extends RxPresenter<ContractHomeItem.View> implements ContractHomeItem.Presenter {


    private ContractHomeItem.Model model ;

    @Override
    public void attachView(ContractHomeItem.View view) {
        super.attachView(view);
        model = new ContractHomeItem.Model() {
            @Override
            public Flowable<RspNewsListBean> getNewList(String curVersion) {
                return ApiProxy.getApi(IIndexApi.class).getNewList("db004bc6c287489a834ea2ff6d5efd78",curVersion);
            }
        };
    }

    @Override
    public void getNewList(String curVersion) {
        observeMain(model.getNewList(curVersion)).safeSubscribe(new NormalSubscriber<RspNewsListBean>(){
            @Override
            public void onNext(RspNewsListBean rspNewsListBean) {
                super.onNext(rspNewsListBean);
                mView.setNewListView(rspNewsListBean);
            }

            @Override
            public void onError(int code, String msg) {
//                super.onError(code, msg);
                mView.setNewListView(null);
            }
        });

    }
}
