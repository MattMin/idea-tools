package com.oeong.data;

public class TimingSetting {

    public static final int DEFAULT_WORK_TIME = 1;
    public static final int DEFAULT_REST_TIME = 2;

    private int workTime = DEFAULT_WORK_TIME;

    private int restTime = DEFAULT_REST_TIME;

    public int getWorkTime() {
        return workTime;
    }

    public void setWorkTime(int workTime) {
        this.workTime = workTime;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public static TimingSetting of(int restTime, int workTime) {
        TimingSetting settings = new TimingSetting();
        settings.setRestTime(restTime);
        settings.setWorkTime(workTime);
        return settings;
    }
}
