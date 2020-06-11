package com.zq.modulemvp.index.presenter;

import com.zq.modulemvp.basemvp.api.ApiProxy;
import com.zq.modulemvp.basemvp.api.NormalSubscriber;
import com.zq.modulemvp.basemvp.base.RxPresenter;
import com.zq.modulemvp.index.api.IIndexApi;
import com.zq.modulemvp.index.bean.RspNewsListBean;
import com.zq.modulemvp.index.contract.ContractHome;

import io.reactivex.Flowable;

/**
 * desc
 * author zhouqi
 * data 2020/6/11
 */
public class HomePresenter extends RxPresenter<ContractHome.View> implements ContractHome.Presenter {


    private ContractHome.Model model ;

    @Override
    public void attachView(ContractHome.View view) {
        super.attachView(view);
        model = new ContractHome.Model() {
            @Override
            public Flowable<RspNewsListBean> getNewList(String curVersion) {
                return ApiProxy.getApi(IIndexApi.class).getNewList(curVersion,"152f22a47215f051bffe887673a5058b");
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
                super.onError(code, msg);
            }
        });

    }
}
