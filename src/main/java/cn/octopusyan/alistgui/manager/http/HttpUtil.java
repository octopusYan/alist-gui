package cn.octopusyan.alistgui.manager.http;

import com.alibaba.fastjson2.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * 网络请求封装
 *
 * @author octopus_yan@foxmail.com
 */
public class HttpUtil {
    private volatile static HttpUtil util;
    private volatile HttpClient httpClient;
    private final HttpConfig httpConfig;

    private HttpUtil(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
        this.httpClient = createClient(httpConfig);
    }

    public static HttpUtil getInstance() {
        if (util == null) {
            throw new RuntimeException("are you ready ?");
        }
        return util;
    }

    public static void init(HttpConfig httpConfig) {
        synchronized (HttpUtil.class) {
            util = new HttpUtil(httpConfig);
        }
    }

    private static HttpClient createClient(HttpConfig httpConfig) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .version(httpConfig.getVersion())
                .connectTimeout(Duration.ofMillis(httpConfig.getConnectTimeout()))
                .sslContext(httpConfig.getSslContext())
                .sslParameters(httpConfig.getSslParameters())
                .followRedirects(httpConfig.getRedirect());
        Optional.ofNullable(httpConfig.getAuthenticator()).ifPresent(builder::authenticator);
        Optional.ofNullable(httpConfig.getCookieHandler()).ifPresent(builder::cookieHandler);
        Optional.ofNullable(httpConfig.getProxySelector()).ifPresent(builder::proxy);
        Optional.ofNullable(httpConfig.getExecutor()).ifPresent(builder::executor);
        return builder.build();
    }

    public HttpUtil proxy(String host, int port) {
        if (httpClient == null)
            throw new RuntimeException("are you ready ?");

        InetSocketAddress unresolved = InetSocketAddress.createUnresolved(host, port);
        ProxySelector other = ProxySelector.of(unresolved);
        this.httpConfig.setProxySelector(other);
        this.httpClient = createClient(httpConfig);
        return this;
    }

    public void clearProxy() {
        if (httpClient == null)
            throw new RuntimeException("are you ready ?");

        httpConfig.setProxySelector(HttpClient.Builder.NO_PROXY);
        httpClient = createClient(httpConfig);
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public String get(String uri, JSONObject header, JSONObject param) throws IOException, InterruptedException {
        HttpRequest.Builder request = getRequest(uri + createFormParams(param), header).GET();
        HttpResponse<String> response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

    public String post(String uri, JSONObject header, JSONObject param) throws IOException, InterruptedException {
        HttpRequest.Builder request = getRequest(uri, header)
                .POST(HttpRequest.BodyPublishers.ofString(param.toJSONString()));
        HttpResponse<String> response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

    public String postForm(String uri, JSONObject header, JSONObject param) throws IOException, InterruptedException {
        HttpRequest.Builder request = getRequest(uri + createFormParams(param), header)
                .POST(HttpRequest.BodyPublishers.noBody());

        HttpResponse<String> response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

    private HttpRequest.Builder getRequest(String uri, JSONObject header) {
        HttpRequest.Builder request = HttpRequest.newBuilder();
        // 请求地址
        request.uri(URI.create(uri));
        // 请求头
        if (header != null && !header.isEmpty()) {
            for (String key : header.keySet()) {
                request.header(key, header.getString(key));
            }
        }
        return request;
    }

    private String createFormParams(JSONObject params) {
        StringBuilder formParams = new StringBuilder();
        if (params == null) {
            return formParams.toString();
        }
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value instanceof String) {
                value = URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8);
                formParams.append("&").append(key).append("=").append(value);
            } else if (value instanceof Number) {
                formParams.append("&").append(key).append("=").append(value);
            } else if (value instanceof List) {
                formParams.append("&").append(key).append("=").append(params.getJSONArray(key));
            } else {
                formParams.append("&").append(key).append("=").append(params.getJSONObject(key));
            }
        }
        if (!formParams.isEmpty()) {
            formParams = new StringBuilder("?" + formParams.substring(1));
        }

        return formParams.toString();
    }
}
