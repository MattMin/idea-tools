package com.oeong.service;

import com.oeong.data.DataCenter;
import com.oeong.data.SettingData;
import com.oeong.task.WorkTask;
import com.oeong.ui.fish.TimingClock;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;

import static com.oeong.data.DataCenter.WORK_TIME_TIP;

public class TimerService {
    public static void saveSetting(String restTime, String workTime) {
        DataCenter.settingData = SettingData.of(
            DataCenter.isInteger(restTime) ? Integer.parseInt(restTime) : SettingData.DEFAULT_REST_TIME,
            DataCenter.isInteger(workTime) ? Integer.parseInt(workTime) : SettingData.DEFAULT_WORK_TIME
        );
    }

    /**
     * 休息倒计时
     */
    public static void restCountDown() {
        DataCenter.restCountDownSecond--;
    }

    /**
     * 初始化休息倒计时间
     */
    public static void initRestCountDown() {
        if (DataCenter.restCountDownSecond == -1) {
            DataCenter.restCountDownSecond = 60 * DataCenter.settingData.getRestTime();
        }
    }

    public static void resetRestCountDown() {
        DataCenter.restCountDownSecond = 60 * DataCenter.settingData.getRestTime();
    }

    /**
     * 设置下一次工作时间
     */
    public static void resetNextWorkTime() {
        DataCenter.nextWorkTime = LocalDateTime.now().plusMinutes(DataCenter.settingData.getRestTime());
    }

    /**
     * 设置下一次休息时间
     */
    public static void resetNextRestTime() {
        DataCenter.nextRestTime = LocalDateTime.now().plusMinutes(DataCenter.settingData.getWorkTime());
    }

    public static String openTimer(TimingClock timingClock) {
        resetNextRestTime();
        // 清除之前的所有任务
        DataCenter.workTimer.cancel();
        // 创建开启定时任务
        DataCenter.workTimer = new Timer();
        DataCenter.workTimer.schedule(new WorkTask(timingClock),
                Date.from(DataCenter.nextRestTime.atZone(ZoneId.systemDefault()).toInstant()));
        DataCenter.status = DataCenter.WORKING;
        return String.format(WORK_TIME_TIP, getDateStr(DataCenter.nextRestTime));
    }

    public static void closeTimer() {
        // 清除之前的所有任务
        DataCenter.workTimer.cancel();
        DataCenter.status = DataCenter.CLOSE;
    }

    public static String getCountDownDesc(int time) {
        if (time > 0) {
            int hour = time / (60 * 60);
            int minute = (time % (60 * 60)) / 60;
            int second = time % 60;
            return "Rest countdown：" + String.format("%s:%s:%s", fillZero(hour), fillZero(minute), fillZero(second));
        }
        return "The rest is over.";
    }

    public static String fillZero(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return "" + time;
    }

    public static String getDateStr(LocalDateTime localDateTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm:ss");
        return df.format(localDateTime);
    }

}
