package com.oeong.tools;

import com.oeong.ui.RandomPwdUI;
import org.jsoup.internal.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class PwdTools {
    private final List<String> words = new ArrayList<>(1000);

    private RandomPwdUI randomPwdUI;

    public PwdTools(RandomPwdUI randomPwd){
        this.randomPwdUI = randomPwd;
        URL wordsFile = getClass().getResource("/META-INF/words.txt");
        try {
            assert wordsFile != null;
            var in = new BufferedReader(new InputStreamReader(wordsFile.openStream()));
            String line = in.readLine();
            while(line!=null && !StringUtil.isBlank(line=line.trim())){
                words.add(line);
                line=in.readLine();
            }
            words.add(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String createPwd(String type){
        if(RandomPwdUI.easy.equals(type)){
            return createEasyPwd();
        }else if(RandomPwdUI.pin.equals(type)){
            return createPinPwd();
        }
        return createRandomPwd();
    }

    public String createRandomPwd(){
        var num = (int)randomPwdUI.getLenBox().getSelectedItem();
        List<Function<Object,Character>> functions = new ArrayList<>();
        functions.add(o->getLowChar());
        if(randomPwdUI.getA09RadioButton().isSelected()){
            functions.add(o->getNumChar());
        }
        if(randomPwdUI.getCapitalRadio().isSelected()){
            functions.add(o->getUpperChar());
        }
        if(randomPwdUI.getSupRadioButton().isSelected()){
            functions.add(o->getSpecialChar());
        }
        functions.add(o->getLowChar());

        List<Character> list = new ArrayList<>(num);
        // 因为已经把 4 种字符放进list了，所以 i 取值从 4开始
        // 产生随机数用于随机调用生成字符的函数
        for (int i = 0; i < num; i++) {
            list.add(getRandomChar(functions));
        }

        Collections.shuffle(list);   // 打乱排序
        StringBuilder stringBuilder = new StringBuilder(list.size());
        for (Character c : list) {
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }


    public String createPinPwd(){
        var num = (int)randomPwdUI.getLenBox().getSelectedItem();
        StringBuilder str = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for(int i=0;i<num;i++){
            str.append(numStr.charAt(random.nextInt(1000)%10));
        }
        return str.toString();
    }


    public String createEasyPwd(){
        var num = (int)randomPwdUI.getLenBox().getSelectedItem();
        SecureRandom random = new SecureRandom();
        var joiner = (String)randomPwdUI.getJoinerBox().getSelectedItem();
        StringBuilder str = new StringBuilder();
        for(int i=0;i<num;i++){
            String word = words.get(random.nextInt(1000));
            str.append(word);
            str.append(joiner);
        }
        if(!str.isEmpty()){
            str.deleteCharAt(str.length()-1);
        }
        return str.toString();
    }


    private static final String lowStr = "abcdefghijklmnopqrstuvwxyz";
    private static final String upStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String specialStr = "~!@#$%^&*()_+/-=[]{};:'<>?.";
    private static final String numStr = "0123456789";

    // 随机获取字符串字符
    private static char getRandomChar(String str) {
        SecureRandom random = new SecureRandom();
        return str.charAt(random.nextInt(str.length()));
    }
    // 随机获取小写字符
    private static char getLowChar() {
        return getRandomChar(lowStr);
    }

    // 随机获取大写字符
    private static char getUpperChar() {
        return getRandomChar(upStr);
    }

    // 随机获取数字字符
    private static char getNumChar() {
        return getRandomChar(numStr);
    }

    // 随机获取特殊字符
    private static char getSpecialChar() {
        return getRandomChar(specialStr);
    }

    private static char getRandomChar(List<Function<Object,Character>> list) {
        SecureRandom random = new SecureRandom();
        int funNum = random.nextInt(list.size());
        return list.get(funNum).apply(null);
    }













}
