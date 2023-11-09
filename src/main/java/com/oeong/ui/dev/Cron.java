package com.oeong.ui.dev;

import cn.hutool.json.JSONUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.oeong.notice.Notifier;
import com.oeong.tools.cron.CronExpressionEx;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Cron {
    private JComboBox<String> type;
    private JTextField expression;
    private JTextArea resultArea;
    @Getter
    private JPanel container;
    private JButton testBt;

    {
        testBt.addActionListener(this::getCron);
        expression.setText("0 0 18 28-31 * ?");
        type.addItemListener(e->{
            String item = (String) e.getItem();
            if("UNIX".equals(item)){
                expression.setText("0 */12 * * *");
            }else if("Java(Spring)".equals(item)){
                expression.setText("0 0 18 28-31 * ?");
            }else if("Quartz".equals(item)){
                expression.setText("0 0 18 L * ?");
            }
        });
        definitionMap.keySet().forEach(type::addItem);
    }
    public void getCron(ActionEvent e){
        resultArea.setText("");
        String text = expression.getText();
        if(StringUtil.isEmpty(text)){
            return;
        }
        String typeItem = (String) type.getSelectedItem();
        String expStr = expression.getText();
        String ret = definitionMap.get(typeItem).apply(expStr);
        resultArea.setText(ret);
    }

    static Map<String, Function<String,String>> definitionMap = new HashMap<>();
    static {
        definitionMap.put("UNIX",Cron::unixCron);
        definitionMap.put("Java(Spring)",Cron::javaCron);
        definitionMap.put("Quartz",Cron::quartz);
    }
    static String reg = "[\\-*/,?0-9]+";
    public static String unixCron(String expStr){
        String[] arr = expStr.split(" ");
        if(expStr.split(" ").length!=5){
            Notifier.notifyError("表达式长度必须等于5");
            return "";
        }
        if(arr[4].equals("*")){
            expStr = expStr.substring(0,expStr.lastIndexOf('*'))+"?";
        }
        try {
            CronExpressionEx exp = new CronExpressionEx("0 "+expStr);
            String dm = exp.getDaysOfMonthExp();
            String dw = exp.getDaysOfWeekExp();
            if(!dm.matches(reg) || !dw.matches(reg)){
                Notifier.notifyError("表达式不支持");
                return "";
            }
            return getDateStr(exp);
        } catch (ParseException e) {
            Notifier.notifyError(e.getMessage());
        }

        return "";
    }

    @NotNull
    private static String getDateStr(CronExpressionEx exp) {
        StringBuilder sb= new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date();
        for(int i=0;i<5;i++){
            d = exp.getNextValidTimeAfter(d);
            if(d==null){
                return sb.toString();
            }
            sb.append(format.format(d)).append("\r\n");
        }
        return sb.toString();
    }

    public static String javaCron(String expStr){
        String[] arr = expStr.split(" ");
        if(arr.length!=6){
            Notifier.notifyError("表达式长度必须等于6");
            return "";
        }
        if(arr[5].equals("*")){
            expStr = expStr.substring(0,expStr.lastIndexOf('*'))+"?";
        }
        try {
            CronExpressionEx exp = new CronExpressionEx(expStr);
            String dm = exp.getDaysOfMonthExp();
            String dw = exp.getDaysOfWeekExp();
            dm = dm.replaceAll("[0-9\\-*,?LW]","");
            dw = dw.replaceAll("[0-9\\-*,?L#]|SUN|MON|TUE|WED|THU|FRI|SAT","");
            if(!dm.isEmpty()){
                Notifier.notifyError("Expression is not supported char "+JSONUtil.toJsonStr(dm.toCharArray()));
            }
            if(!dw.isEmpty()){
                Notifier.notifyError("Expression is not supported char "+JSONUtil.toJsonStr(dm.toCharArray()));
                return "";
            }
            return getDateStr(exp);
        } catch (ParseException e) {
            Notifier.notifyError(e.getMessage());
        }
        return null;
    }

    public static String quartz(String expStr){
        try {
            CronExpressionEx exp = new CronExpressionEx(expStr);
            return getDateStr(exp);
        } catch (ParseException e) {
            Notifier.notifyError(e.getMessage());
        }
        return null;
    }

}
