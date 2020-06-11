package com.zq.modulemvp.basemvp.base;

import java.util.concurrent.TimeUnit;

/**
 * desc
 * author zhouqi
 * data 2020/6/3
 */
public class Config {
    public static final int VIEW_THROTTLE_TIME = 2;
    public static final TimeUnit VIEW_THROTTLE_UNIT = TimeUnit.SECONDS;

    /** 300 ms */
    public static final int TRANSACTION_DURATION = 300;
    /** route flag login */
    public static final int EXTRA_FLAG_LOGIN = 0x01;
    /** have a wechat name only, user in invalidate sate */
    public static final int BIND_STATUS_WX = 0;
    /** a validate user of calligraphy */
    public static final int BIND_STATUS_PHONE = 10;
    /** login component arg */
    public static final String ARG_TARGET = "path";
    public static final String ARG_BIND_STATUS = "status";
    public static final String ARG_BIND_USERID = "user_id";
    /** the original request path arg, process it after login succeed */
    public static final String ARG_ORIGIN = "origin";
    public static final String ARG_FRAG_ANIM = "animator";
    public static final String ARG_FRAG_TITLE = "frag_title";
    public static final String ACTION_WECHAT_LOGIN = "app.action.login.wechat";
    public static final String ACTION_IMG_PICK = "app.action.img.pick";

    public static final String SPLASH_VIEWED = "app.splash.view.state";
    public static final String APP_REGISTER_URL = "app.h5.register";
    public static final String APP_SERVICE_INFO = "app.custom.service.info";
    public static final String UPDATE_NEED_LOGIN = "update.need.login";
    public static final boolean FEATURE_TRC20 = false;

    /** network timeout, unit seconds */
    public static final int CONNECT_TIMEOUT = 60;
    public static final int READ_TIMEOUT = 60;
    public static final int WRITE_TIMEOUT = 60;
    public static final TimeUnit NET_TIME_OUT_UNIT = TimeUnit.SECONDS;

    public static final int API_SUCCEED_CODE = 200;
    public static final int API_CODE_VOTE_ABORT = 412;
    public static final int SEARCH_DEBOUNCE = 500;
    public static final int REQ_TIME_OUT = 30 * 1000;
    public static final TimeUnit SEARCH_DEBOUNCE_UNIT = TimeUnit.MILLISECONDS;

    public static final int COIN_TYPE_USDT = 1;
    public static final int COIN_TYPE_FN = 2;

    public static final int PAGE_SIZE = 20;
    public static final int API_NET_LOST = -400;
}
