package cn.octopusyan.alistgui.manager.http;

import cn.octopusyan.alistgui.enums.ProxySetup;
import cn.octopusyan.alistgui.model.ProxyInfo;
import cn.octopusyan.alistgui.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

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
import java.util.Map;
import java.util.Optional;

/**
 * 网络请求封装
 *
 * @author octopus_yan@foxmail.com
 */
public class HttpUtil {
    private volatile static HttpUtil util;
    @Getter
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

    public void proxy(ProxySetup setup, ProxyInfo proxy) {
        if (httpClient == null)
            throw new RuntimeException("are you ready ?");

        switch (setup) {
            case NO_PROXY -> clearProxy();
            case SYSTEM -> httpConfig.setProxySelector(ProxySelector.getDefault());
            case MANUAL -> {
                InetSocketAddress unresolved = InetSocketAddress.createUnresolved(proxy.getHost(), Integer.parseInt(proxy.getPort()));
                httpConfig.setProxySelector(ProxySelector.of(unresolved));
            }
        }

        this.httpClient = createClient(httpConfig);
    }

    public void clearProxy() {
        if (httpClient == null)
            throw new RuntimeException("are you ready ?");

        httpConfig.setProxySelector(HttpClient.Builder.NO_PROXY);
        httpClient = createClient(httpConfig);
    }

    public String get(String uri, JsonNode header, JsonNode param) throws IOException, InterruptedException {
        HttpRequest.Builder request = getRequest(uri + createFormParams(param), header).GET();
        HttpResponse<String> response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

    public String post(String uri, JsonNode header, JsonNode param) throws IOException, InterruptedException {
        HttpRequest.Builder request = getRequest(uri, header)
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.toJsonString(param)));
        HttpResponse<String> response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

    public String postForm(String uri, JsonNode header, JsonNode param) throws IOException, InterruptedException {
        HttpRequest.Builder request = getRequest(uri + createFormParams(param), header)
                .POST(HttpRequest.BodyPublishers.noBody());

        HttpResponse<String> response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

    private HttpRequest.Builder getRequest(String uri, JsonNode header) {
        HttpRequest.Builder request = HttpRequest.newBuilder();
        // 请求地址
        request.uri(URI.create(uri));
        // 请求头
        if (header != null && !header.isEmpty()) {
            for (Map.Entry<String, JsonNode> property : header.properties()) {
                String key = property.getKey();
                request.header(key, JsonUtil.toJsonString(property.getValue()));
            }
        }
        return request;
    }

    private String createFormParams(JsonNode params) {
        StringBuilder formParams = new StringBuilder();
        if (params == null) {
            return formParams.toString();
        }
        for (Map.Entry<String, JsonNode> property : params.properties()) {
            String key = property.getKey();
            JsonNode value = params.get(key);
            if (value.isTextual()) {
                String value_ = URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8);
                formParams.append("&").append(key).append("=").append(value_);
            } else if (value.isNumber()) {
                formParams.append("&").append(key).append("=").append(value);
            } else if (value.isArray()) {
                formParams.append("&").append(key).append("=").append(JsonUtil.toJsonString(value));
            } else {
                formParams.append("&").append(key).append("=").append(JsonUtil.toJsonString(value));
            }
        }
        if (!formParams.isEmpty()) {
            formParams = new StringBuilder("?" + formParams.substring(1));
        }

        return formParams.toString();
    }
}
