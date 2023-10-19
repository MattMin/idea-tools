package com.oeong.service;

import com.oeong.data.TimingCenter;
import com.oeong.data.TimingSetting;
import com.oeong.task.WorkTask;
import com.oeong.ui.fish.TimingClock;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.regex.Pattern;

import static com.oeong.data.TimingCenter.WORK_TIME_TIP;

public class TimingService {
    public static void saveSetting(String restTime, String workTime) {
        TimingCenter.timingSetting = TimingSetting.of(Integer.parseInt(restTime), Integer.parseInt(workTime));
    }

    /**
     * countdown to rest time
     */
    public static void restCountDown() {
        TimingCenter.restCountDownSecond--;
    }

    /**
     * init rest countdown
     */
    public static void initRestCountDown() {
        if (TimingCenter.restCountDownSecond == -1) {
            TimingCenter.restCountDownSecond = 60 * TimingCenter.timingSetting.getRestTime();
        }
    }

    /**
     * reset rest countdown
     */
    public static void resetRestCountDown() {
        TimingCenter.restCountDownSecond = 60 * TimingCenter.timingSetting.getRestTime();
    }

    /**
     * reset next work time
     */
    public static void resetNextWorkTime() {
        TimingCenter.nextWorkTime = LocalDateTime.now().plusMinutes(TimingCenter.timingSetting.getRestTime());
    }

    /**
     * reset next rest time
     */
    public static void resetNextRestTime() {
        TimingCenter.nextRestTime = LocalDateTime.now().plusMinutes(TimingCenter.timingSetting.getWorkTime());
    }

    public static String openWorkTimer(TimingClock timingClock) {
        // rest and close timer
        resetNextRestTime();
        TimingCenter.workTimer.cancel();
        // create a new work timer
        TimingCenter.workTimer = new Timer();
        TimingCenter.workTimer.schedule(new WorkTask(timingClock),
                Date.from(TimingCenter.nextRestTime.atZone(ZoneId.systemDefault()).toInstant()));
        TimingCenter.status = TimingCenter.WORKING;
        return String.format(WORK_TIME_TIP, getDateStr(TimingCenter.nextRestTime));
    }

    /**
     * close work timer
     */
    public static void closeWorkTimer() {
        TimingCenter.workTimer.cancel();
        TimingCenter.status = TimingCenter.CLOSE;
    }

    /**
     * seconds to HH:mm:ss desc
      */
    public static String getCountDownDesc(int seconds) {
        if (seconds > 0) {
            int hour = seconds / (60 * 60);
            int minute = (seconds % (60 * 60)) / 60;
            int second = seconds % 60;
            return "Rest countdown: " + String.format("%s:%s:%s", fillZero(hour), fillZero(minute), fillZero(second));
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

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
