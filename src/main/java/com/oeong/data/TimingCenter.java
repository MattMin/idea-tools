package com.oeong.data;

import java.time.LocalDateTime;
import java.util.Timer;

public class TimingCenter {
    public static final Integer CLOSE = 0;
    public static final Integer WORKING = 1;
    public static final Integer RESTING = 2;
    public static TimingSetting timingSetting;
    public static Integer status = CLOSE;
    public static LocalDateTime nextWorkTime;
    public static LocalDateTime nextRestTime;
    public static Timer workTimer = new Timer();
    public static Timer restTimer = new Timer();
    public static int restCountDownSecond = -1;
    public static final String RUNNING_TIP = "The work is running. (ง ˙o˙)ว";
    public static final String STOPPED_TIP = "The work stopped. ʕ•̫͡• ʔ•̫͡•ཻʕ•̫͡•ʔ•͓͡•ʔ";
    public static String WORK_TIME_TIP = "The work starts at %s and ends at %s.°꒰๑'ꀾ'๑꒱°✨";
    public static final String REST_TIME_TIP = "It's rest time. ฅ՞•ﻌ•՞ฅ";
    public static final String WORK_TIP = "I want to keep working! ヽ( ຶ▮ ຶ)ﾉ!!!✨";
    public static final String REST_TIP = "I need more rest. ˃̣̣̥᷄⌓˂̣̣̥᷅";
    public static final String STOP_TIP = "I don't want to work. ꒦ິ^꒦ິ";
}
