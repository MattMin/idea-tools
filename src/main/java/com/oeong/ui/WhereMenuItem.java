//package com.oeong.ui;
//
//import com.alibaba.fastjson.JSONArray;
//import com.intellij.openapi.project.Project;
//import com.oeong.ui.fish.HowToWhere;
//import org.apache.http.HttpHost;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.util.EntityUtils;
//import org.jetbrains.annotations.NotNull;
//
//import javax.swing.*;
//import java.awt.event.ActionEvent;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
///**
// * @descriptions:
// * @author: Zzw
// * @date: 2023/9/15 9:17
// */
//public class WhereMenuItem implements MenuAction {
//    /**
//     * 当前菜单名称
//     *
//     * @return
//     */
//    @Override
//    public @NotNull String getName() {
//        return "where";
//    }
//
//    /**
//     * 父级菜单名称
//     *
//     * @return 对应 MenuAction.FISH,MenuAction.AI,MenuAction.DEV,MenuAction.TOOLS
//     */
//    @Override
//    public @NotNull String parent() {
//        return FISH;
//    }
//
//    /**
//     * 子菜单排序
//     *
//     * @return 数字越大越靠后
//     */
//    @Override
//    public int order() {
//        return 0;
//    }
//
//    /**
//     * 菜单点击动作
//     *
//     * @param event 事件对象
//     */
//    @Override
//    public void action(ActionEvent event) {
//        //获取地点信息
//        HttpGet get = new HttpGet("https://raw.githubusercontent.com/MattMin/idea-tools/dev/assets/menu.json");
//        CloseableHttpClient client = HttpClientBuilder.create().useSystemProperties().build();
//        CloseableHttpResponse response = null;
//        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
//        requestConfigBuilder
//                .setSocketTimeout(5000)
//                .setConnectTimeout(5000)
//                .setConnectionRequestTimeout(5000);
//        //添加代理
//        requestConfigBuilder.setProxy(new HttpHost("127.0.0.1", 7890));
//        get.setConfig(requestConfigBuilder.build());
//        try {
//            response = client.execute(get);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        String responseBody = null;
//        try {
//            responseBody =EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//        JSONArray jsonArray = JSONArray.parseArray(responseBody);
//
//
//    }
//
//    /**
//     * 子菜单对应的面板
//     *
//     * @param project 当前工程
//     * @return 面板对象
//     */
//    @Override
//    public @NotNull JPanel getContainer(Project project) {
//        HowToWhere howToWhere = new HowToWhere();
//        return howToWhere.getPanel1();
//    }
//}
