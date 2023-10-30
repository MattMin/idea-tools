package com.oeong.tools;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/10/17 9:42
 */
public class AliyunTools {

    public static String send(String appcode, String bodys) {
        String host = "https://gjbsb.market.alicloudapi.com";
        String path = "/ocrservice/advanced";

        HttpPost post = new HttpPost(host + path);
        CloseableHttpClient client = HttpClientBuilder.create().useSystemProperties().build();
        CloseableHttpResponse response = null;
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000);
        //添加代理
//        requestConfigBuilder.setProxy(new HttpHost("127.0.0.1", 7890));
        post.setConfig(requestConfigBuilder.build());
        //根据API的要求，定义相对应的Content-Type
        post.setHeader("Content-Type", "application/json; charset=UTF-8");
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        post.setHeader("Authorization", "APPCODE " + appcode);

        if (bodys != null) {
            StringEntity stringEntity = new StringEntity(bodys, ContentType.APPLICATION_JSON);
            post.setEntity(stringEntity);
        }

        try {

            response = client.execute(post);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
