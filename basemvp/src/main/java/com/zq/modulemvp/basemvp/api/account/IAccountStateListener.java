package com.zq.modulemvp.basemvp.api.account;

import androidx.annotation.MainThread;

/**
 * desc
 * author zhouqi
 * data 2020/6/10
 */
public interface IAccountStateListener {
    @MainThread
    void onLoginStateChanged(LoginState state);

    @MainThread
    void onUserInfoChanged(User user);
}