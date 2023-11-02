package com.oeong.tools;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/10/17 10:26
 */
public class BaiduTools {


    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     *
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    public static String getAuth(String ak, String sk) {

//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "");
//        Request request = new Request.Builder()
//                .url("https://aip.baidubce.com/oauth/2.0/token?client_id=O13Oeu****yMmIG2&client_secret=nyOubk****VGDESl&grant_type=client_credentials")
//                .method("POST", body)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Accept", "application/json")
//                .build();
//        Response response = HTTP_CLIENT.newCall(request).execute();
//        return null;
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 2. 官网获取的 API Key
                + "client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk
                // 1. grant_type为固定参数
                + "&grant_type=client_credentials";
        try {
            HttpPost post = new HttpPost(getAccessTokenUrl);
            CloseableHttpClient client = HttpClientBuilder.create().useSystemProperties().build();
            CloseableHttpResponse response = null;
            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
            requestConfigBuilder
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000);
            JSONObject postData = new JSONObject();
            postData.set("client_id", ak);
            postData.set("client_secret", sk);
            postData.set("grant_type", "client_credentials");

            StringEntity stringEntity = new StringEntity(postData.toString(), StandardCharsets.UTF_8);
            post.setConfig(requestConfigBuilder.build());
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setEntity(stringEntity);
            response = client.execute(post);
            String bodys = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            JSONObject jsonObject = JSONUtil.parseObj(bodys);
            return jsonObject.getStr("access_token");
        } catch (Exception e) {

        }
        return null;
    }

    public static String send(String ak, String sk, String bodys) {

        String host = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic?access_token=" + getAuth(ak, sk);

        HttpPost post = new HttpPost(host);
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
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            if (bodys != null) {
                Map<String, String> map = JSONUtil.toBean(bodys, Map.class);
                List<NameValuePair> nameValuePairs = getNameValuePairList(map);
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
                /*发送json数据需要设置contentType*/
                urlEncodedFormEntity.setContentType("application/x-www-form-urlencoded");
                post.setEntity(urlEncodedFormEntity);
            }
            response = client.execute(post);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {

        }
        return null;
    }

    private static List<NameValuePair> getNameValuePairList(Map<String, String> map) {
        List<NameValuePair> list = new ArrayList<>();
        for(String key : map.keySet()) {
            list.add(new BasicNameValuePair(key,map.get(key)));
        }

        return list;
    }
}
