package com.oeong.ui.fish;

import com.oeong.service.TimerService;

import javax.swing.*;

import static com.oeong.data.DataCenter.RUNNING_TIP;
import static com.oeong.data.DataCenter.STOPPED_TIP;

// TODO: 2023/10/11 下面的文字大一点

public class TimingClock {
    public JRadioButton startRadioButton;
    public JRadioButton stopRadioButton;
    public JTextField workField;
    public JTextField restField;
    private JPanel mainPanel;
    private JLabel statusTipLabel;
    public JLabel timeTipLabel;

    public TimingClock() {

        statusTipLabel.setText(STOPPED_TIP);

        // status button
        startRadioButton.addActionListener(e -> {
            if (startRadioButton.isSelected()) {
                chooseStartButton();
            } else {
                chooseStopButton();
            }
            handle(this);
        });
        stopRadioButton.addActionListener(e -> {
            if (stopRadioButton.isSelected()) {
                chooseStopButton();
            } else {
                chooseStartButton();
            }
            handle(this);
        });
    }

    public void chooseStartButton() {
        startRadioButton.setSelected(true);
        stopRadioButton.setSelected(false);
        startRadioButton.setText("Running");
        stopRadioButton.setText("Stop");
        statusTipLabel.setText(RUNNING_TIP);
    }

    public void chooseStopButton() {
        stopRadioButton.setSelected(true);
        startRadioButton.setSelected(false);
        startRadioButton.setText("Start");
        stopRadioButton.setText("Stopped");
        statusTipLabel.setText(STOPPED_TIP);
        timeTipLabel.setText("");
    }

    public void handle(TimingClock timingClock) {
        TimerService.saveSetting(restField.getText(), workField.getText());

        if (startRadioButton.isSelected()) {
            String notification = TimerService.openTimer(timingClock);
            timeTipLabel.setText(notification);
            JOptionPane.showMessageDialog(null, notification, "tips", JOptionPane.INFORMATION_MESSAGE);
        } else {
            TimerService.closeTimer();
        }
    }

    public JPanel getComponent() {
        return mainPanel;
    }
}
