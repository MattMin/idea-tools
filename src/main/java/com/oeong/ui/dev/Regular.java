package com.oeong.ui.dev;

import com.intellij.openapi.util.text.StringUtil;
import lombok.Getter;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regular {
    private JComboBox<String> commonRegBox;

    @Getter
    private JPanel container;
    private JTextPane matchField;
    private JTextField tarArea;
    private JTextField regField;


    {
        Map<String,String> commonReg=new HashMap<>();
        commonReg.put("Match Chinese string","[\\u4e00-\\u9fa5]");
        commonReg.put("Double-byte characters","[^\\x00-\\xff]");
        commonReg.put("Blank lines","\\s");
        commonReg.put("Email address","\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");
        commonReg.put("Website URL","^((https|http|ftp|rtsp|mms)?:\\/\\/)[^\\s]+");
        commonReg.put("Mobile phone (domestic)","0?(13|14|15|17|18|19)[0-9]{9}");
        commonReg.put("WPhone number (domestic)","[0-9-()（）]{7,18}");
        commonReg.put("Negative floating point number","-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)");
        commonReg.put("Match integers","-?[1-9]\\d*");
        commonReg.put("Positive floating point number","[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*");
        commonReg.put("Tencent QQ account","[1-9]([0-9]{5,11})");
        commonReg.put("Postal code","\\d{6}");
        commonReg.put("IP","(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)");
        commonReg.put("ID number","\\d{17}[\\d|x]|\\d{15}");
        commonReg.put("Format date","\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2}");
        commonReg.put("Positive integer","[1-9]\\d*");
        commonReg.put("Negative integer","-[1-9]\\d*");
        commonReg.put("Username","[A-Za-z0-9_\\-\\u4e00-\\u9fa5]+");
        commonRegBox.addItem("");
        commonReg.keySet().forEach(commonRegBox::addItem);
        // 常用正则，下拉框组装
        commonRegBox.addItemListener(e->{
            String type = (String) e.getItem();
            String reg = commonReg.get(type);
            regField.setText(reg);
        });
        // 正则匹配
        tarArea.addCaretListener(e->{
            String str = match(regField.getText(), tarArea.getText());
            matchField.setText(str);
        });
//        matchButton.addActionListener(l->{
//            String str = match(regField.getText(), tarArea.getText());
//            matchField.setText(str);
//        });

    }

    public String match(String pattern,String target){
        if (StringUtil.isNotEmpty(pattern) && StringUtil.isNotEmpty(target)) {
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(target);
            int matcher_start = 0;
            StringBuilder sb = new StringBuilder();
            while (m.find(matcher_start)) {
                sb.append(m.group()).append("\r");
                matcher_start = m.end();
            }
            return sb.toString();
        }
        return "";
    }
}
