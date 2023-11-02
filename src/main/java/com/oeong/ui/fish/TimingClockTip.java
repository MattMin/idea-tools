package com.oeong.ui.fish;

import com.oeong.data.TimingCenter;
import com.oeong.notice.Notifier;
import com.oeong.service.TimingService;
import com.oeong.task.RestTask;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Timer;

import static com.oeong.data.TimingCenter.REST_TIP;
import static com.oeong.data.TimingCenter.STOP_TIP;
import static com.oeong.data.TimingCenter.WORK_TIP;

public class TimingClockTip extends JDialog {
    private JRadioButton workRadioButton;
    private JRadioButton restRadioButton;
    private JRadioButton stopRadioButton;
    private JButton okButton;
    private JLabel countdownLabel;
    private JPanel contentPane;
    private JLabel statusLabel;


    public TimingClockTip(TimingClock timingClock) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(okButton);
        setTitle("Timing Clock");
        setLocation(400, 200); // 距离屏幕左上角的真实位置
        setSize(500, 150); // 对话框的长宽

        statusLabel.setText(WORK_TIP);

        // radio button
        ButtonGroup group = new ButtonGroup();
        group.add(workRadioButton);
        group.add(restRadioButton);
        group.add(stopRadioButton);

        workRadioButton.addActionListener(e -> statusLabel.setText(WORK_TIP));
        restRadioButton.addActionListener(e -> statusLabel.setText(REST_TIP));
        stopRadioButton.addActionListener(e -> statusLabel.setText(STOP_TIP));

        // OK button
        okButton.addActionListener(e -> onOK(timingClock));

        // Bind close button event
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // new and start rest timer
        TimingCenter.restTimer = new Timer();
        TimingCenter.restTimer.schedule(new RestTask(timingClock, this), new Date(), 1000);
    }

    private void onOK(TimingClock timingClock) {
        if (workRadioButton.isSelected()) {
            // close previous rest timer
            TimingCenter.restTimer.cancel();
            TimingService.resetRestCountDown();

            // new work timer
            String notification = TimingService.openWorkTimer(timingClock);
            timingClock.timeTipLabel.setText(notification);
            Notifier.notifyInfo(notification);

            // set start status
            TimingCenter.status = TimingCenter.WORKING;
            timingClock.chooseStartButton();
            timingClock.startRadioButton.setSelected(true);

            dispose();
        } else if (restRadioButton.isSelected()) {
            // close previous rest timer
            TimingCenter.restTimer.cancel();
            TimingService.resetRestCountDown();

            // reset next rest time
            TimingService.resetNextRestTime();

            // new rest timer
            TimingCenter.restTimer = new Timer();
            TimingCenter.restTimer.schedule(new RestTask(timingClock, this), new Date(), 1000);
        } else {
            // if it stops, close all timer
            TimingCenter.workTimer.cancel();
            TimingCenter.restTimer.cancel();
            TimingCenter.restCountDownSecond = -1;

            // set close status
            TimingCenter.status = TimingCenter.CLOSE;
            timingClock.chooseStopButton();
            timingClock.stopRadioButton.setSelected(true);

            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }

    public void setDesc(String desc) {
        countdownLabel.setText(desc);
    }
}
