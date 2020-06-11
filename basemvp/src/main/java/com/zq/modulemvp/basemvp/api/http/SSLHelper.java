package com.zq.modulemvp.basemvp.api.http;

import android.content.res.AssetManager;

import androidx.annotation.NonNull;


import com.zq.basemvp.util.FileUtil;
import com.zq.basemvp.util.IOUtils;
import com.zq.modulemvp.basemvp.api.ApiProxy;
import com.zq.modulemvp.basemvp.util.AppLog;
import com.zq.modulemvp.basemvp.util.AppUtil;
import com.zq.modulemvp.common.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * desc
 * author zhouqi
 * data 2020/6/8
 */
public class SSLHelper {

    private static final String X509 = "X.509";
    private static final String CA_ALIAS = "ca";
    private static final String SSL = "SSL";
    // for public test https://httpbin.org/
    public static final String CER_HTTP_BIN_NAME = "httpbin.pem";
    public static final String CHONG_HOLE_CER_NAME = "app_server.pem";
    public static final String OSS_API_CER_NAME = "oss.pem";

    @NonNull
    public static X509TrustManager getTrustManager() {
        X509TrustManager trustManager = null;
        try {
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            TrustManager[] tms = tmf.getTrustManagers();
            if (tms.length != 1 || !(tms[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected managers:" + Arrays.toString(tms));
            }
            trustManager = (X509TrustManager) tms[0];
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            AppLog.e("getTrustManager exception occurred:" + e);
        }
        if (trustManager == null) {
            throw new NullPointerException("trustManager is null, please check the certificate");
        }
        return trustManager;
    }

    private static KeyStore getKeyStore() {
        KeyStore keyStore = null;
        try {
            String keyStoreType = KeyStore.getDefaultType();
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
        } catch (Exception e) {
            AppLog.e("Error during getting keystore", e);
        }
        return keyStore;
    }

    private static KeyStore injectCert(String[] fileNames) throws Exception {
        KeyStore keyStore = getKeyStore();
        if (keyStore == null) {
            AppLog.e("Error keyStore is null");
            return null;
        }
        AssetManager assetManager = AppUtil.getContext().getAssets();
        CertificateFactory cf = CertificateFactory.getInstance(X509);
        for (int i = 0; i < fileNames.length; i++) {
            String f = fileNames[i];
            InputStream caInput = null;
            try {
                caInput = assetManager.open(f);
                Certificate ca = cf.generateCertificate(caInput);
                keyStore.setCertificateEntry(CA_ALIAS + (i), ca);
            } catch (Exception e) {
                AppLog.e("Failed to load certificate file " + f);
            } finally {
                if (caInput != null) {
                    caInput.close();
                }
            }
        }
        return keyStore;
    }

    public static SSLContext getSslContextForCertificateFile(String[] fileName) {
        try {
            KeyStore keyStore = injectCert(fileName);
            SSLContext sslContext = SSLContext.getInstance(SSL);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            String msg = "Error during creating SslContext for certificate from assets";
            AppLog.e(msg, e);
            throw new RuntimeException(msg);
        }
    }

    public static SSLContext getSslContextForInputStream(InputStream is) {
        try {
            KeyStore keyStore = injectCert(is);
            SSLContext sslContext = SSLContext.getInstance(SSL);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            String msg = "Error during creating SslContext for certificate from input stream";
            AppLog.e(msg, e);
            throw new RuntimeException(msg);
        }
    }

    private static KeyStore injectCert(InputStream fIs) {
        KeyStore keyStore = getKeyStore();
        if (keyStore == null) {
            AppLog.e("Error keyStore is null");
            return null;
        }
        if (fIs == null) {
            return null;
        }
        try {
            CertificateFactory cf = CertificateFactory.getInstance(X509);
            Certificate ca = cf.generateCertificate(fIs);
            // starting from 100
            keyStore.setCertificateEntry(CA_ALIAS + (100), ca);
        } catch (Exception e) {
            AppLog.e("Failed to load certificate input stream");
        } finally {
            IOUtils.closeSilence(fIs);
        }
        return keyStore;
    }

    public static void processCert() {
        if ("Product".equals(AppUtil.getMetaDataByKey(AppUtil.getContext(), "APP_CHANNEL"))) {
            processCert(false);
        }
    }

    /**
     * handle ssl certificate file
     * @param update true if you want to replace the cert file
     */
    public static void processCert(boolean update) {
        final String certName = ".cdsl.cert";
        AppLog.enableDebug(true);
        File check = new File(AppUtil.getContext().getCacheDir(), certName);
        HttpsURLConnection connection = null;
        try {
            if (check.exists() && !update) {
                injectCertToApiProxy(new FileInputStream(check));
                return;
            }
            final String url = "https://cdsl.oss-cn-zhangjiakou.aliyuncs.com/sign/cdsl.crt";
            // final String url = "https://cdsl.oss-cn-zhangjiakou.aliyuncs.com/sign/domain.cer";
            connection = (HttpsURLConnection) new URL(url).openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode >= HttpURLConnection.HTTP_MULT_CHOICE) {
                connection.disconnect();
                AppLog.w("dz[httpURLConnectionGet 300]");
            }
            connection.connect();
            AppLog.i("init from net");
            File certFile = saveToFile(connection.getInputStream(), certName);
            injectCertToApiProxy(new FileInputStream(certFile));
        } catch (Exception e) {
            AppLog.e("open connection failed.", e);
        }
        AppLog.enableDebug(BuildConfig.DEBUG);
        if (connection == null) {
            AppLog.e("open connection failed.");
        }
    }

    private static void injectCertToApiProxy(InputStream is) {
        ApiProxy.injectHttps(is);
        AppLog.i("init cert end");
     }

    private static File saveToFile(InputStream is, String fileName) throws IOException {
        AppLog.i("file is download" + Thread.currentThread());
        File check = new File(AppUtil.getContext().getCacheDir(), fileName);
        File cached;
        if (!check.exists()) {
            cached = FileUtil.createFile(check);
        } else {
            cached = check;
        }
        FileOutputStream fos = new FileOutputStream(cached);
        int len = 0;
        byte[] buff = new byte[1024];
        try {
            while ((len = is.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
        } catch (Exception e) {
            AppLog.e("save cert file failed:" + e);
        } finally {
            IOUtils.closeSilence(fos);
        }
        return cached;
    }

    /**
     * get app-server certificate
     *
     * @return ssl socket factory
     */
    public static SSLSocketFactory getSSLFactory() {
        return getSSLFactory(CHONG_HOLE_CER_NAME);
    }

    public static SSLSocketFactory getSSLFactory(String file) {
        return getSSLFactory(new String[]{file});
    }

    public static SSLSocketFactory getSSLFactory(String[] files) {
        return getSslContextForCertificateFile(files).getSocketFactory();
    }

    /**
     * TODO
     *
     * @deprecated
     */
    private static class MyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
