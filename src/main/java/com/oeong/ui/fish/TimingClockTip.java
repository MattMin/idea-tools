package com.oeong.ui.fish;

import com.oeong.data.DataCenter;
import com.oeong.service.TimerService;
import com.oeong.task.RestTask;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Timer;

import static com.oeong.data.DataCenter.REST_TIP;
import static com.oeong.data.DataCenter.STOP_TIP;
import static com.oeong.data.DataCenter.WORK_TIP;
import static com.oeong.service.TimerService.resetNextRestTime;

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

        okButton.addActionListener(e -> onOK(timingClock));

        //绑定关闭按钮事件
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        DataCenter.reskTimer = new Timer();
        DataCenter.reskTimer.schedule(new RestTask(timingClock, this), new Date(), 1000);
    }

    private void onOK(TimingClock timingClock) {
        if (workRadioButton.isSelected()) {
            DataCenter.reskTimer.cancel();
            TimerService.resetRestCountDown();

            timingClock.handle(timingClock);

            String notifyStr = TimerService.openTimer(timingClock);
            timingClock.timeTipLabel.setText(notifyStr);

            dispose();
        } else if (restRadioButton.isSelected()) {
            DataCenter.reskTimer.cancel();
            TimerService.resetRestCountDown();

            resetNextRestTime();

            DataCenter.reskTimer = new Timer();
            DataCenter.reskTimer.schedule(new RestTask(timingClock, this), new Date(), 1000);
        } else {
            DataCenter.workTimer.cancel();
            DataCenter.reskTimer.cancel();
            DataCenter.restCountDownSecond = -1;

            DataCenter.status = DataCenter.CLOSE;
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
