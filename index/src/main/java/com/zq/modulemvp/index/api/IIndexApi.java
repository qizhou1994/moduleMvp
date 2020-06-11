package com.zq.modulemvp.index.api;

import com.zq.modulemvp.index.bean.RspNewsListBean;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * desc
 * author zhouqi
 * data 2020/6/10
 */
public interface IIndexApi {

    /**
     *
     * @param type
     * 	类型,top(头条，默认),shehui(社会),guonei(国内),guoji(国际),yule(娱乐),
     * 	tiyu(体育)junshi(军事),keji(科技),caijing(财经),shishang(时尚)
     * @return
     */
    @GET("http://v.juhe.cn/toutiao/index")
    Flowable<RspNewsListBean> getNewList(@Query("type") String type,@Query("key") String key);
}
