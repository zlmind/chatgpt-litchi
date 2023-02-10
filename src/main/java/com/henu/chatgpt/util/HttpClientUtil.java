package com.henu.chatgpt.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @author qz
 * @Description
 */
@Slf4j
public class HttpClientUtil {

    private static SSLContext sslContext;

    static {
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    return true;
                }
            }).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }


    private static final RequestConfig DEFAULT_REQUEST_CONFIG = RequestConfig.custom()
            .setConnectTimeout(50000)
            .setSocketTimeout(500000)
            .setConnectionRequestTimeout(50000)
            .build();

    /**
     * 10 connections per domain
     * 100 total
     */
    private static final CloseableHttpClient HTTPCLIENT = HttpClients.custom()
            .setDefaultRequestConfig(DEFAULT_REQUEST_CONFIG)
            .setMaxConnPerRoute(100)
            .setMaxConnTotal(1000)
            .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE))
            .build();

    /**
     * @param url
     * @param body, json format request data
     */
    public static JSONObject sendHttp(String url, String body, Map<String, String> header) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }
        httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
        StringEntity entity = new StringEntity(body, Charset.defaultCharset());
        httpPost.setEntity(entity);
        HttpResponse response = null;
        try {
            response = HTTPCLIENT.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return JSONObject.parseObject(result);
            } else {
                JSONObject jo = JSONObject.parseObject(result);
                jo.put("errorMsg", jo.get("error"));
                return jo;
            }
        } catch (Throwable t) {
            log.error("sendHttp exception: ", t);
            throw new RuntimeException(t);
        } finally {
            if (null != response) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException ignored) {
                }
            }
        }
    }
}
