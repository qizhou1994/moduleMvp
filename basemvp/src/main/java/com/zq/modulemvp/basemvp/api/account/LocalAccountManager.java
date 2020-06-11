package com.zq.modulemvp.basemvp.api.account;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.tencent.mmkv.MMKV;
import com.zq.basemvp.util.CollectionUtils;
import com.zq.basemvp.util.JsonUtil;
import com.zq.modulemvp.basemvp.base.UiHandler;
import com.zq.modulemvp.basemvp.util.AppLog;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class LocalAccountManager {
    public static final String TAG = "LocalAccountInfoManager";
    private static final String MMKV_CRYPT_KEY = "dG9rZW4=";
    private static final String MMKV_FILE_ID = "user_info_mmap_id";
    private static String ACCOUNT_INFO = "account_info";
    private static String TOKEN = "token";
    private static String UID = "uid";

    private static LocalAccountManager sInstance;

    private AtomicReference<User> user = new AtomicReference<>();
    private AtomicReference<String> token = new AtomicReference<>();
    private AtomicReference<String> uid = new AtomicReference<>();

    private List<WeakReference<IAccountStateListener>> mAccountStateListener = new CopyOnWriteArrayList<>();

    @NonNull
    private MMKV kv;

    private LocalAccountManager() {
        kv = MMKV.mmkvWithID(MMKV_FILE_ID, MMKV.SINGLE_PROCESS_MODE, MMKV_CRYPT_KEY);
    }

    public static LocalAccountManager getInstance() {
        if (sInstance == null) {
            synchronized (LocalAccountManager.class) {
                if (sInstance == null) {
                    sInstance = new LocalAccountManager();
                }
            }
        }
        return sInstance;
    }

    public synchronized void addAccountStateListener(IAccountStateListener listener) {
        if (listener != null && !containListener(listener)) {
            mAccountStateListener.add(new WeakReference<>(listener));
        }
    }

    private synchronized boolean containListener(IAccountStateListener listener) {
        CollectionUtils.TraversalReduce<Boolean> result = new CollectionUtils.TraversalReduce<>(false);
        CollectionUtils.traversalWeakRefListAndRemoveEmpty(
                mAccountStateListener,
                result,
                (index, item, reduce) -> {
                    reduce.data |= listener == item;
                    return !reduce.data;
                });

        return result.data;
    }

    private void notifyUserInfoChanged() {
        User user = getUser();
        CollectionUtils.traversalWeakRefListAndRemoveEmpty(mAccountStateListener, (index, item) -> {
            AppLog.i("item:" + item + ", index:" + index + ", size:" + mAccountStateListener
                    .size());
            UiHandler.post(() -> item.onUserInfoChanged(user));
            return true;
        });
    }

    private void notifyLoginStateChanged() {
        LoginState state = getLoginState();
        CollectionUtils.traversalWeakRefListAndRemoveEmpty(mAccountStateListener, (index, item) -> {
            AppLog.i("item:" + item + ", index:" + index + ", size:" + mAccountStateListener
                    .size());
            UiHandler.post(() -> item.onLoginStateChanged(state));
            return true;
        });
    }

    public LoginState getLoginState() {
        User user = getUser();
        if (user != null && !TextUtils.isEmpty(getToken())) {
            return LoginState.FULL_LOGIN;
        }
        return LoginState.NOT_LOGIN;
    }

    public void saveUser(User user) {
        if (user == null) {
            return;
        }
        this.user.set(user);
        String info = JsonUtil.object2json(user);
        kv.encode(ACCOUNT_INFO, info);
        notifyUserInfoChanged();
    }

    /**
     * save auth token
     *
     * @param uid user id
     * @param token the auth token
     */
    public void saveLoginAuth(final String uid, final String token) {
        this.uid.set(uid);
        this.token.set(token);
        kv.encode(UID, uid);
        kv.encode(TOKEN, token);
        kv.apply();
        notifyLoginStateChanged();
    }

    /**
     * get user info
     */
    public User getUser() {
        if (user.get() == null) {
            String info = kv.decodeString(ACCOUNT_INFO, "");
            // do not print user info, be careful
            AppLog.v("refreshUI user getUser:" + info);
            if (!TextUtils.isEmpty(info)) {
                user.set(JsonUtil.json2object(info, User.class));
            }
        }
        return user.get();
    }

    public String getToken() {
        if (token.get() == null) {
            String token = kv.decodeString(TOKEN, "");
            this.token.set(token);
        }
        return token.get();
    }

    public String getUid() {
        if (uid.get() == null) {
            String uid = kv.decodeString(UID, "");
            this.uid.set(uid);
        }
        return uid.get();
    }

    public boolean isLogin() {
        return getUser() != null && !TextUtils.isEmpty(getAuthorization().trim());
    }

    /**
     * To get authorization, append to the HTTP header usually.
     */
    public String getAuthorization() {
        String uid = getUid();
        return (uid) + " " + getToken();
    }

    public void clearLoginUser() {
        boolean reset = false;
        if (user.get() != null) {
            reset = true;
            user.set(null);
        }
        if (!TextUtils.isEmpty(uid.get())) {
            reset = true;
            uid.set(null);
        }
        if (!TextUtils.isEmpty(token.get())) {
            reset = true;
            token.set(null);
        }
        kv.removeValueForKey(ACCOUNT_INFO);
        kv.removeValueForKey(TOKEN);
        kv.removeValueForKey(UID);
        AppLog.i("clear user, reset " + reset);
        if (reset) {
            notifyLoginStateChanged();
            notifyUserInfoChanged();
        }
    }
}
