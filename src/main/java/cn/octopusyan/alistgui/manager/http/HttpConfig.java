package cn.octopusyan.alistgui.manager.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executor;

/**
 * Http配置参数
 *
 * @author octopus_yan@foxmail.com
 */
public class HttpConfig {
    private static final Logger logger = LoggerFactory.getLogger(HttpConfig.class);
    /**
     * http版本
     */
    private HttpClient.Version version = HttpClient.Version.HTTP_2;

    /**
     * 转发策略
     */
    private HttpClient.Redirect redirect = HttpClient.Redirect.NORMAL;

    /**
     * 线程池
     */
    private Executor executor;

    /**
     * 认证
     */
    private Authenticator authenticator;

    /**
     * 代理
     */
    private ProxySelector proxySelector;

    /**
     * CookieHandler
     */
    private CookieHandler cookieHandler;

    /**
     * sslContext
     */
    private SSLContext sslContext;

    /**
     * sslParams
     */
    private SSLParameters sslParameters;

    /**
     * 连接超时时间毫秒
     */
    private int connectTimeout = 10000;

    /**
     * 默认读取数据超时时间
     */
    private int defaultReadTimeout = 1200000;


    public HttpConfig() {
        TrustManager[] trustAllCertificates = new X509TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0]; // Not relevant.
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // TODO Auto-generated method stub
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // TODO Auto-generated method stub
            }
        }};
        sslParameters = new SSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm("");


        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");//取消主机名验证
            sslContext.init(null, trustAllCertificates, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("", e);
        }

    }


    public HttpClient.Version getVersion() {
        return version;
    }

    public void setVersion(HttpClient.Version version) {
        this.version = version;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }


    public HttpClient.Redirect getRedirect() {
        return redirect;
    }

    public void setRedirect(HttpClient.Redirect redirect) {
        this.redirect = redirect;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public ProxySelector getProxySelector() {
        return proxySelector;
    }

    public void setProxySelector(ProxySelector proxySelector) {
        this.proxySelector = proxySelector;
    }

    public CookieHandler getCookieHandler() {
        return cookieHandler;
    }

    public void setCookieHandler(CookieHandler cookieHandler) {
        this.cookieHandler = cookieHandler;
    }

    public int getDefaultReadTimeout() {
        return defaultReadTimeout;
    }

    public void setDefaultReadTimeout(int defaultReadTimeout) {
        this.defaultReadTimeout = defaultReadTimeout;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public SSLParameters getSslParameters() {
        return sslParameters;
    }
}
