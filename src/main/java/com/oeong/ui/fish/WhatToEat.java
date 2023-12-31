package com.oeong.ui.fish;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;

import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class WhatToEat {
    private JButton startButton;
    private JPanel mainPanel;
    private JLabel whatLabel;
    private JButton stopButton;

    JSONArray foodArray;

    public JPanel getComponent() {
        return mainPanel;
    }

    public WhatToEat() {
        initFoodList();

        Timer foodTimer = new Timer(100, e -> {
            Object o = foodArray.get((int) (Math.random() * foodArray.size()));
            String food = o.toString();
            whatLabel.setText(food);
        });

        // choose food randomly
        startButton.addActionListener(e -> {
            foodTimer.start();
        });

        stopButton.addActionListener(e -> {
            foodTimer.stop();
        });
    }

    public void initFoodList() {
        // String menuUrl = "https://raw.githubusercontent.com/MattMin/idea-tools/dev/assets/menu.json";
        String menuUrl = "https://oeong.com/assets/file/menu.json";
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(menuUrl))
                .GET()
                .build();

        new Thread(() -> {
            String content;
            try {
                content = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            } catch (Exception e) {
                content = "[\"包菜洋葱拌馓子\",\"馓子鸡蛋拌包菜\",\"青蟹烧土豆\",\"鲢鱼头烧粉皮\",\"素烧白菜油豆腐条\",\"清蒸皮皮虾\",\"茄汁土豆牛腩\",\"头水紫菜香菇蒸肉末\",\"猪心拌马齿苋\",\"鸡腿肉拌紫甘蓝\",\"香香的橙子松饼\",\"蚝油秋葵\",\"番茄酱\",\"冰皮月饼\",\"鸽胗拌黄瓜\",\"素烧冬瓜\",\"玉米南瓜炖排骨\",\"布朗尼\",\"虾仁玉米拌生菜\",\"鲍鱼汁烧竹笋\",\"西蓝花饼\",\"拍荷兰黄瓜\",\"醪糟蛋花汤\",\"麻辣鸡丝\",\"瘦肉炒新竹米粉\",\"老式拉丝面包\",\"黄豆酱带鱼\",\"手撕琵琶腿肉拌青瓜\",\"鲜炒乌米饭\",\"辣焖鲳鱼\",\"绿豆芽炒豆腐\",\"火龙果小奶糕\",\"黑米饭团\",\"胡萝卜鲜肉馅馄饨\",\"洋葱炒豆腐卷\",\"榨菜丝四季豆炒茭白\",\"三豆豆浆\",\"土豆泥拌饭\",\"苦瓜炒牛肉\",\"桑叶炒蛋\",\"葡萄柠檬茶\",\"罗汉果陈皮薄荷茶\",\"荷兰豆拌鸽胸肉\",\"番茄粉丝虾滑\",\"爆炒牛肉\",\"番茄鸡蛋饼\",\"凉拌牛肉\",\"清淡豆芽汤\",\"凉拌海带丝\",\"酸辣土豆丝\",\"大蒜炒黄瓜\",\"西芹炒虾仁腰果\",\"砂锅拌饭\",\"自制条糕\",\"四季豆牛肉馅饼\",\"鸽胸肉拌西兰花\",\"油麦菜炒肉片\",\"荔枝虾球\",\"杂豆玉米粥\",\"酱牛肉\",\"返沙麦香姜薯\",\"番茄烧滑牛肉\",\"排骨蒸贝贝南瓜\",\"黄米凉糕\",\"蒜汁鸡腿\",\"红烧小龙虾\",\"毛豆肉饼汤\",\"凉拌茄子\",\"干锅包菜\",\"素熬鲜蚕豆\",\"双味竹节蛏\",\"鲜肉粽\",\"卤鸡腿\",\"芹菜凉拌木耳\",\"肉末豆腐炒生菜\",\"包菜牛肉炒米粉\",\"多巴胺水果芋圆\",\"番茄酸菜牛肉面\",\"菌菇排骨汤\",\"虫草花香菇蒸鸡\",\"西红柿鸡蛋刀削面\",\"丝瓜炒黑豆腐竹\",\"虾皮炒辣子\",\"烤韭菜\",\"南瓜炒青椒\",\"家常炒饼\",\"青椒蒜末汁淋蒸鱼\",\"康乃馨烧麦\",\"海参青菜小米粥\",\"桑叶春饼\",\"酸辣豆瓣汤\",\"明月草炒绿豆芽\",\"青椒腐乳炒番薯叶\",\"猴头菇炒莴笋片\",\"五花肉炒豌豆\",\"黄瓜拌西红柿洋葱\",\"家常凉菜\",\"爽口酸辣土豆片\",\"腐竹焖五花肉\",\"早餐卤蛋热面\",\"炒酱肉\",\"生煎梅菜包\",\"荔枝酸奶冻\",\"丝瓜豆腐烧油条\",\"香菇菜芯炒鲜虾\",\"捞汁海虾\",\"黑芝麻糊布丁\",\"擂椒皮蛋\",\"炸藕夹\",\"盐焗五花肉\",\"凉拌佛手瓜\",\"生菜煎蛋汤\",\"低脂鸡肉脆\",\"豆沙早餐面包\",\"小白菜炒肉\",\"空炸五花肉串\",\"金枪鱼塔塔\",\"木耳精肉炒韭苔\",\"竹笋拌鸡丝\",\"豆沙小餐包\",\"蛎肉拌马齿苋\",\"鸽胸肉拌黄瓜\",\"干煎马哈鱼\",\"五指毛桃鸡汤\",\"蔬菜鸡蛋饼\",\"绿豆芽炒鸡蛋\",\"香辣黑椒土豆\",\"蒜蓉生菜\"]";
            }
            foodArray = JSONUtil.parseArray(content);
        }).start();
    }
}
