package com.oeong.task;

import com.oeong.data.DataCenter;
import com.oeong.service.TimerService;
import com.oeong.ui.fish.TimingClock;
import com.oeong.ui.fish.TimingClockTip;

import javax.swing.*;
import java.util.TimerTask;

import static com.oeong.data.DataCenter.REST_TIME_TIP;

public class RestTask extends TimerTask {

    TimingClock timingClock;
    TimingClockTip timingClockTip;

    public RestTask(TimingClock timingClock, TimingClockTip timingClockTip) {
        this.timingClock = timingClock;
        this.timingClockTip = timingClockTip;
    }

    @Override
    public void run() {
        //设置下次工作时间（休息结束的时间）
        TimerService.resetNextWorkTime();
        //初始化休息倒计时
        TimerService.initRestCountDown();
        TimerService.restCountDown();//倒计时
        if (DataCenter.restCountDownSecond >= 0) { //休息时间内
            String desc = TimerService.getCountDownDesc(DataCenter.restCountDownSecond);
            timingClockTip.setDesc(String.format(REST_TIME_TIP + " %s", desc));
        } else {//休息时间结束
            DataCenter.reskTimer.cancel();   //关闭定时器
            timingClockTip.dispose(); //关闭提示窗口
            String notification = TimerService.openTimer(timingClock);// 开启工作计时器

            JOptionPane.showMessageDialog(null, notification, "tips", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
