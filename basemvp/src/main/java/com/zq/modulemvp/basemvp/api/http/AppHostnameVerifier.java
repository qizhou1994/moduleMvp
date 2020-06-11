package com.zq.modulemvp.basemvp.api.http;

import android.text.TextUtils;

import com.zq.modulemvp.basemvp.util.AppLog;
import com.zq.modulemvp.basemvp.util.AppUtil;

import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * desc
 * author zhouqi
 * data 2020/6/8
 */
public class AppHostnameVerifier implements HostnameVerifier {
    private static final String KEY_SERVER_URL = "SERVER_URL";
    private static final List<String> HOSTS = Arrays.asList(
            "39.98.149.29", "116.62.153.208",
            "cdsl.oss-cn-zhangjiakou.aliyuncs.com",
            "wxpay.wxutil.com",
            "cdapk.etyun.top",
            "httpbin.org");

    private static String DOMAIN_URL;
    static {
        DOMAIN_URL = AppUtil.getMetaDataByKey(AppUtil.getContext(), KEY_SERVER_URL);
    }
    @Override
    public boolean verify(String hostname, SSLSession session) {
        AppLog.v("host = " + hostname);
        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
        boolean defVerify = hv.verify(hostname, session);
        return defVerify || validate(hostname, session);
    }

    private boolean validate(String hostName, SSLSession session) {
        if (TextUtils.isEmpty(hostName)) {
            return false;
        }
        return HOSTS.contains(hostName) || DOMAIN_URL.contains(hostName);
    }
}
