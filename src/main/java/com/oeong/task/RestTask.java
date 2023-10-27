package com.oeong.task;

import com.oeong.data.TimingCenter;
import com.oeong.notice.Notifier;
import com.oeong.service.TimingService;
import com.oeong.ui.fish.TimingClock;
import com.oeong.ui.fish.TimingClockTip;

import javax.swing.*;
import java.util.TimerTask;

import static com.oeong.data.TimingCenter.REST_TIME_TIP;

public class RestTask extends TimerTask {

    TimingClock timingClock;
    TimingClockTip timingClockTip;

    public RestTask(TimingClock timingClock, TimingClockTip timingClockTip) {
        this.timingClock = timingClock;
        this.timingClockTip = timingClockTip;
    }

    @Override
    public void run() {
        // TimingService.resetNextWorkTime();
        TimingService.initRestCountDown();
        TimingService.restCountDown();
        if (TimingCenter.restCountDownSecond >= 0) {
            // If, during the rest time, set timingClock tip
            String desc = TimingService.getCountDownDesc(TimingCenter.restCountDownSecond);
            timingClockTip.setDesc(String.format(REST_TIME_TIP + " %s", desc));
        } else {
            // the rest time is over, close rest timer
            TimingCenter.restTimer.cancel();
            timingClockTip.dispose();

            // open work timer
            String notification = TimingService.openWorkTimer(timingClock);
            timingClock.timeTipLabel.setText(notification);
            Notifier.notifyInfo(notification);
            JOptionPane.showMessageDialog(null, notification, "Timing Clock", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
