package com.oeong.task;

import com.oeong.data.TimingCenter;
import com.oeong.service.TimingService;
import com.oeong.ui.fish.TimingClock;
import com.oeong.ui.fish.TimingClockTip;

import java.util.TimerTask;

public class WorkTask extends TimerTask {

    TimingClock timingClock;

    public WorkTask(TimingClock timingClock) {
        this.timingClock = timingClock;
    }

    @Override
    public void run() {
        TimingCenter.status = TimingCenter.RESTING;
        TimingService.resetNextWorkTime();
        // display timingClockTip page
        TimingClockTip timingClockTip = new TimingClockTip(timingClock);
        timingClockTip.setVisible(true);
    }
}
