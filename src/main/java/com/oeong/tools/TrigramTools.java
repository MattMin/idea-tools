 package com.oeong.tools;

 import cn.hutool.json.JSONObject;
 import cn.hutool.json.JSONUtil;

 import java.io.IOException;
 import java.net.InetSocketAddress;
 import java.net.ProxySelector;
 import java.net.URI;
 import java.net.http.HttpClient;
 import java.net.http.HttpRequest;
 import java.net.http.HttpResponse;
 import java.time.Duration;
 import java.util.*;

 /**
1、没有动爻：直接使用本卦卦辞来断。
2、一个动爻：根据动爻的爻辞来断。
3、两个动爻：分这两种情况：
    a. 如果两个动爻都是阳爻或都是阴爻，就取最上面动爻的爻辞断（注意是从下往上看）。
    b. 如果是一阴一阳两个动爻，就取阴爻的爻辞来断。
4、三个动爻：取三条动爻中间的这一爻来断，不分阴阳。
5、四个动爻：看其余的两个非动爻，哪个非动爻的位置在最下方，就取最下爻的爻辞来断。
6、五个动爻：直接取那个不是动爻的那一爻的爻辞来断。
**/
public class TrigramTools {
    private static HashMap allTrigram;
    static {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://oeong.com/assets/file/trigram.json"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        new Thread(()->{
            HttpResponse<String> str = null;
            try {
                str = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
            }
            if(str==null){
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("https://raw.githubusercontent.com/MattMin/idea-tools/dev/assets/trigram.json"))
                        .timeout(Duration.ofSeconds(5))
                        .GET()
                        .build();
                try {
                    str = client.send(req, HttpResponse.BodyHandlers.ofString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            allTrigram = JSONUtil.toBean(str.body(), HashMap.class);
        }).start();
    }
    public  TrigramVo divine(String prophecy){
        if(allTrigram==null){
            throw new RuntimeException("initialization failed");
        }
        Random r = new Random();
        LinkedHashMap<Integer,Boolean> arr = new LinkedHashMap<>();
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<6;i++){
            int f1 = r.nextBoolean()?1:0;
            int f2 = r.nextBoolean()?1:0;
            int f3 = r.nextBoolean()?1:0;
            if(f1==f2 && f2==f3){
                arr.put(i+1,f1==1);
            }
            sb.append((f1+f2+f3)%2);
        }
        int dif=0;
        if(arr.size()==1){
            dif = arr.keySet().stream().findFirst().get();
        } else if(arr.size()==2){
            Boolean[] bs = arr.values().toArray(new Boolean[0]);
            boolean flag = bs[0] == bs[1];
            if(flag){
                dif= (int)arr.keySet().toArray()[1];
            }else{
                dif = (int)arr.keySet().toArray()[0];
            }
        }else if(arr.size()==3){
            dif =(int)arr.keySet().toArray()[1];
        }else if(arr.size()==4){
            Set<Integer> tem = arr.keySet();
            for(int i=1;i<=6;i++){
                if(!tem.contains(i)){
                    dif=i;
                    break;
                }
            }
        }else if(arr.size()==5){
            Set<Integer> tem = arr.keySet();
            for(int i=1;i<=6;i++){
                if(!tem.contains(i)){
                    dif=i;
                    break;
                }
            }
        }
        JSONObject tri = (JSONObject) allTrigram.get(sb.toString());
        JSONObject tar = (JSONObject) tri.get(dif + "");
        TrigramVo vo = new TrigramVo();
        vo.setImg(tri.get("img").toString());
        vo.setName(tri.get("name").toString());
        vo.setOriginal(tar.get("原文").toString());
        vo.setProphecy(tar.get(prophecy).toString());
        return vo;
    }



}
