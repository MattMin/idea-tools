package com.oeong.task;

import com.oeong.data.DataCenter;
import com.oeong.ui.fish.TimingClock;
import com.oeong.ui.fish.TimingClockTip;

import java.time.LocalDateTime;
import java.util.TimerTask;

public class WorkTask extends TimerTask {

    TimingClock timingClock;

    public WorkTask(TimingClock timingClock) {
        this.timingClock = timingClock;
    }

    @Override
    public void run() {
        DataCenter.status = DataCenter.RESTING;
        DataCenter.nextWorkTime = LocalDateTime.now().plusMinutes(DataCenter.settingData.getRestTime());
        TimingClockTip timingClockTip = new TimingClockTip(timingClock);
        timingClockTip.setVisible(true);
    }
}
