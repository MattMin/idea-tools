package com.oeong.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @descriptions:
 * @author: Admin
 * @date: 2023/10/16 10:17
 * @version: 1.0
 */

public enum ApiServerTypeEnum {
    aliyun(1,"阿里云"),
    baidu(2,"百度");

    private int type;
    private String description;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    private ApiServerTypeEnum(int type, String description) {
        this.description = description;
        this.type = type;
    }

    @Override
    public String toString() {
        return this.description;
    }


    public static List<Integer> getConnectionTypeList() {
        List<Integer> connectionTypeList = new ArrayList<>();
        for (ApiServerTypeEnum e : values()) {
            connectionTypeList.add(e.getType());
        }
        return connectionTypeList;
    }
}
