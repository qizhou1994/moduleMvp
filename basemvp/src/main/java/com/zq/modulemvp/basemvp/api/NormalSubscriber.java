package com.zq.modulemvp.basemvp.api;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.JsonParseException;
import com.zq.modulemvp.basemvp.R;
import com.zq.modulemvp.basemvp.api.account.LocalAccountManager;
import com.zq.modulemvp.basemvp.api.fail.ResponseException;
import com.zq.modulemvp.basemvp.base.Config;
import com.zq.modulemvp.basemvp.base.Constants;
import com.zq.modulemvp.basemvp.base.ToastUtil;
import com.zq.modulemvp.basemvp.base.UiHandler;
import com.zq.modulemvp.basemvp.util.ActivityStackUtil;
import com.zq.modulemvp.basemvp.util.AppLog;
import com.zq.modulemvp.basemvp.util.AppUtil;
import com.zq.modulemvp.common.BuildConfig;

import java.util.List;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.subscribers.DisposableSubscriber;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;
import retrofit2.HttpException;

/**
 * desc
 * author zhouqi
 * data 2020/6/10
 */
public class NormalSubscriber<T> extends DisposableSubscriber<T> {

    private static final int STATUS_TOKEN_INVALID = 403;
    private static final int STATUS_VOTE_ABORTED = 412;
    private static final int STATUS_USER_FROZEN = 413;
    private static final int STATUS_USER_REMOVED = 414;
    private static final int STATUS_SERVER_BUSY = 500;
    private static final int JSON_PARSE_FAILED = -1000;
    private static final int HTTPS_FAILED = -2000;
    private static final int UNKNOWN = -1001;

    private boolean useCacheIfOffline;

    public NormalSubscriber() {
        this(true);
    }

    public NormalSubscriber(boolean useCacheIfNeed) {
        useCacheIfOffline = useCacheIfNeed;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!AppUtil.isNetWorkAvailable(AppUtil.getContext())) {
            final String netError = AppUtil.getContext().getString(R.string.business_net_error);
            UiHandler.post(() -> {
                ToastUtil.showToastShort(netError);
            });
            if (!isDisposed() && !useCacheIfOffline) {
                dispose();
                onError(Config.API_NET_LOST, netError);
            }
        }
    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {
        AppLog.e("onError:" + e.toString());
        if (e instanceof ResponseException) {
            int errCode = ((ResponseException) e).getCode();
//            if (errCode == STATUS_USER_FROZEN) {
//                triggerFrozen(e.getMessage());
//            } else if (errCode == STATUS_VOTE_ABORTED) {
//                triggerVote(((ResponseException) e).getData());
//            } else
                if (errCode == STATUS_TOKEN_INVALID || errCode == STATUS_USER_REMOVED) {
                // TODO token invalid
                LocalAccountManager.getInstance().clearLoginUser();
                triggerLogin();
            } else if (errCode == STATUS_SERVER_BUSY) {
                UiHandler.postDelayed(() -> {
                    // ToastUtil.showToastShort("" + e.getMessage());
                    ToastUtil.showToastShort(AppUtil.getContext().getString(R.string.business_busy));
                }, 10);
            }
            onError(((ResponseException) e).getCode(), e.getMessage());
        } else if (e instanceof JsonParseException) {
            onError(JSON_PARSE_FAILED, e.getMessage());
        } else if (e instanceof HttpException) {
            UiHandler.postDelayed(() -> {
                if (BuildConfig.DEBUG) {
                    ToastUtil.showToastShort("errCode:" + (((HttpException) e).code()));
                }
            }, 10);
            // onError(Config.API_NET_LOST, e.getMessage());
        } else if (e instanceof SSLHandshakeException) {
            AppLog.e("ssl handshake failed, please check the .cert file");
            onError(HTTPS_FAILED, AppUtil.getContext().getString(R.string.business_https));
        } else {
            onError(UNKNOWN, AppUtil.getContext().getString(R.string.business_unknown));
        }
    }

    private void triggerLogin() {
        Object obj = ARouter.getInstance()
                .build(Constants.Account.Route.LOGIN)
                .navigation();
        if (obj instanceof SupportFragment) {
            Activity act = ActivityStackUtil.getInstance().getTopActivity();
            if (act instanceof SupportActivity) {
                ((SupportActivity) act).start((ISupportFragment) obj, ISupportFragment.SINGLETOP);
            }
        }
    }

    private void triggerVote(Object data) {
     /*   if (!(data instanceof VoteInfo)) {
            return;
        }
        VoteInfo voteInfo = (VoteInfo) data;
        Object obj = ARouter.getInstance()
                .build(Constants.Account.Route.VOTE)
                .withParcelable(Constants.Account.ARG_VOTE_INFO, voteInfo)
                .navigation();
        if (obj instanceof SupportFragment) {
            Activity act = ActivityStackUtil.getInstance().getTopActivity();
            if (act instanceof SupportActivity) {
                FragmentManager fm = ((SupportActivity) act).getSupportFragmentManager();
                List<Fragment> ff = fm.getFragments();
                FragmentTransaction tr = fm.beginTransaction();
                for (Fragment f : ff) {
                    tr.remove(f);
                }
                ((SupportActivity) act).start((ISupportFragment) obj, ISupportFragment.SINGLETOP);
            }
        }*/
    }

    private void triggerFrozen(String msg) {
     /*   Activity act = ActivityStackUtil.getInstance().getTopActivity();
        if (act instanceof SupportActivity) {
            FragmentManager fm = ((SupportActivity) act).getSupportFragmentManager();
            List<Fragment> ff = fm.getFragments();
            FragmentTransaction tr = fm.beginTransaction();
            for (Fragment f : ff) {
                tr.remove(f);
            }
            ((SupportActivity) act).start(FrozenFragment.newInstance(msg), ISupportFragment.SINGLETOP);
        }*/
    }

    public void onError(int code, String msg) {
        // hide this code if you do not show a error tips by '@Override'
        UiHandler.postDelayed(() -> {
            ToastUtil.showToastShort(msg);
        }, 10);
    }

    @Override
    public void onComplete() {

    }
}
