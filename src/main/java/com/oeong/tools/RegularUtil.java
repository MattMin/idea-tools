package com.oeong.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularUtil {

/*    public static void main(String[] args) {
        String pattern = "([\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?)";
        String str = "test@qq.com,test@163.com";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        int matcher_start=0;
        while (m.find(matcher_start)){
            System.out.println(m.group(0));
            matcher_start = m.end();
        }
    }*/

    public String match(String pattern,String target){
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(target);
        int matcher_start=0;
        StringBuilder sb =new StringBuilder();
        while (m.find(matcher_start)){
            sb.append(m.group()).append("\r");
            matcher_start = m.end();
        }
        return sb.toString();
    }


}
