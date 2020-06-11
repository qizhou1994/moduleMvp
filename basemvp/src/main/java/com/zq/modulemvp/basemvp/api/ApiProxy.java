package com.zq.modulemvp.basemvp.api;

import android.text.TextUtils;

import com.zq.modulemvp.basemvp.AppData;
import com.zq.modulemvp.basemvp.api.account.LocalAccountManager;
import com.zq.modulemvp.basemvp.base.Config;
import com.zq.modulemvp.basemvp.util.AppLog;
import com.zq.modulemvp.basemvp.util.AppUtil;
import com.zq.modulemvp.common.BuildConfig;
import com.zq.modulemvp.basemvp.api.http.SSLHelper;
import com.zq.modulemvp.basemvp.api.http.AppHostnameVerifier;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.ConnectionSpec;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.snakydesign.watchtower.interceptor.WatchTowerInterceptor;

/**
 * desc
 * author zhouqi
 * data 2020/6/9
 */
public class ApiProxy {

    public static final String HEADER_TOKEN = "token";
    public static final String HEADER_UID = "userId";
    public static final String HEADER_VERSION = "version";
    public static final String HEADER_APP_ID = "appid";

    private static final boolean PRINT_LOG = AppLog.isEnableDebug();
    private static final String KEY_CHANNEL = "APP_CHANNEL";
    private static final String KEY_SERVER_URL = "SERVER_URL";
    private static final String CHANNEL_TEST = "Test";
    private static final int APP_ID = 3001;
    // will enable when Server done.
    private static final boolean HTTPS_VERIFY = true;
    private static String DOMAIN_URL;

    private static Retrofit sRetrofit = null;

    static {
        DOMAIN_URL = AppUtil.getMetaDataByKey(AppUtil.getContext(), KEY_SERVER_URL);
    }

    public static final String API_URL = DOMAIN_URL + "/";

    static class InstanceHolder {
        final static ApiProxy sInstance = new ApiProxy();
    }

    public static ApiProxy getInstance() {
        return ApiProxy.InstanceHolder.sInstance;
    }

    private ApiProxy() {
        sRetrofit = initRetrofit(API_URL);
    }

    private Retrofit initRetrofit(final String baseUri) {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor((message) -> {
            AppLog.v("retrofit = " + message);
        });

        if (PRINT_LOG) {
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        Interceptor baseInterceptor = chain -> {
            Request request = chain.request();
            if (!AppUtil.isNetWorkAvailable(AppUtil.getContext())) {
                /*
                 * read the last 5 days http response when offline
                 */
                int maxStale = 60 * 60 * 24 * 5;
                CacheControl tempCacheControl = new CacheControl.Builder()
                        .onlyIfCached()
                        .maxStale(maxStale, TimeUnit.SECONDS)
                        .build();
                request = request.newBuilder()
                        .cacheControl(tempCacheControl)
                        .build();
                AppLog.i("intercept:no network ");
            }
            return chain.proceed(request);
        };

        Interceptor headerInterceptor = chain -> {
            String authorization = LocalAccountManager.getInstance().getAuthorization();
            Request.Builder builder = chain.request().newBuilder();
            AppLog.v(LocalAccountManager.getInstance().getAuthorization());
            if (!TextUtils.isEmpty(authorization)) {
                String uid = LocalAccountManager.getInstance().getUid();
                String token = LocalAccountManager.getInstance().getToken();
                builder.addHeader(HEADER_TOKEN, token);
                builder.addHeader(HEADER_UID, uid);
                builder.addHeader(HEADER_VERSION, AppData.getVersionName());
            }

            builder.addHeader(HEADER_APP_ID, String.valueOf(APP_ID));

            Response response = chain.proceed(builder.build());
            return response;
        };

        // Note for the base retry interceptor {@link okhttp3.RealCall#retryAndFollowUpInterceptor}
        Interceptor rewriteCacheControlInterceptor = chain -> {
            Request request = chain.request();

            Response originalResponse = chain.proceed(request);
            // 3s, online cache
            int maxAge = 3;
            // origin request cache time.
            String reqCache = request.header("Cache-Control");
            if (!TextUtils.isEmpty(reqCache) && reqCache.contains("=")) {
                String age = reqCache.split("=")[1];
                maxAge = Integer.parseInt(age);
            }
            // response request cache time.
            Headers headers = originalResponse.headers();
            String maxAgeStr = headers.get("Cache-Control");
            if (!TextUtils.isEmpty(maxAgeStr) && maxAgeStr.contains("=")) {
                String age = maxAgeStr.split("=")[1];
                maxAge = Integer.parseInt(age);
            }
            return originalResponse.newBuilder()
                    /* Clear the header information, it will return some interference
                     * information if the server doesn't support.
                     * */
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        };

        // http cache, internal file system
        File httpCacheDirectory = new File(AppUtil.getContext().getCacheDir(), "responses");
        /// external file system
        // File httpCacheExDirectory = new File(AppUtil.getContext().getExternalCacheDir(), "responses");
        // max cache size: 100MB
        int cacheSize = 100 * 1024 * 1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(Config.CONNECT_TIMEOUT, Config.NET_TIME_OUT_UNIT)
                .readTimeout(Config.READ_TIMEOUT, Config.NET_TIME_OUT_UNIT)
                .writeTimeout(Config.WRITE_TIMEOUT, Config.NET_TIME_OUT_UNIT)
                .addInterceptor(logInterceptor)
                .addInterceptor(baseInterceptor)
                .addInterceptor(headerInterceptor)
                .addNetworkInterceptor(rewriteCacheControlInterceptor);
        // setting SocketFactory for okhttp client to check Server validate
        if (HTTPS_VERIFY) {

            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .allEnabledTlsVersions()
                    .allEnabledCipherSuites()
                    .build();

            final String[] certs = new String[] { SSLHelper.OSS_API_CER_NAME };

            builder.connectionSpecs(Arrays.asList(spec, ConnectionSpec.CLEARTEXT))
                    .sslSocketFactory(SSLHelper.getSSLFactory(certs), SSLHelper.getTrustManager())
                    .hostnameVerifier(new AppHostnameVerifier());
        }
        // only visible on debug version
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new WatchTowerInterceptor());
        }
        OkHttpClient okHttpClient = builder.build();

        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(new EmptyConverter())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUri)
                .build();
    }

    public synchronized static void injectHttps(InputStream is) {
        OkHttpClient client = getInstance().getClient();
        OkHttpClient fixedClient = client.newBuilder()
                .sslSocketFactory(
                        SSLHelper.getSslContextForInputStream(is).getSocketFactory(),
                        SSLHelper.getTrustManager())
                .build();
        sRetrofit = sRetrofit.newBuilder()
                .client(fixedClient)
                .build();
    }

    /**
     *
     * @param apiService api interface
     * @param <T> the interface class-type
     * @return mostly are flowable
     */
    public static <T> T getApi(final Class<T> apiService) {
        return getInstance().getInstanceApi(apiService);
    }

    public <T> T getInstanceApi(final Class<T> service) {
        return sRetrofit.create(service);
    }

    public OkHttpClient getClient() {
        Call.Factory factory = sRetrofit.callFactory();
        if (factory instanceof OkHttpClient) {
            OkHttpClient oldClient = (OkHttpClient) factory;
            // return oldClient.newBuilder().build();
            return oldClient;
        }
        return null;
    }
}
