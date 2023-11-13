package com.oeong.gpt;

import com.oeong.gpt.ui.MainPanel;
import com.oeong.gpt.ui.MessageComponent;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public abstract class AbstractHandler {

    protected X509TrustManager trustManager;

    public abstract <T> T handle(MainPanel mainPanel, MessageComponent component, String question);

    public static Proxy getProxy() {
        Proxy proxy = null;
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        if (!instance.enableProxy) {
            return null;
        }
        return proxy;
    }

    public HostnameVerifier getHostNameVerifier() {
        return (hostname, session) -> true;
    }

    public SSLContext getSslContext() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[] { getTrustAllManager() }, new java.security.SecureRandom());
        return sslContext;
    }

    public TrustManager getTrustAllManager() {
        if (trustManager != null) {
            return trustManager;
        }
        trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
        };
        return trustManager;
    }
}
