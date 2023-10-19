package com.oeong.ui.fish;

import com.oeong.notice.Notifier;
import com.oeong.service.TimingService;

import javax.swing.*;

import static com.oeong.data.TimingCenter.RUNNING_TIP;
import static com.oeong.data.TimingCenter.STOPPED_TIP;

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
        workField.setEnabled(false);
        restField.setEnabled(false);
    }

    public void chooseStopButton() {
        stopRadioButton.setSelected(true);
        startRadioButton.setSelected(false);
        startRadioButton.setText("Start");
        stopRadioButton.setText("Stopped");
        statusTipLabel.setText(STOPPED_TIP);
        timeTipLabel.setText("");
        workField.setEnabled(true);
        restField.setEnabled(true);
    }

    public void handle(TimingClock timingClock) {
        // set and validate the input
        boolean saved = TimingService.saveSetting(restField.getText(), workField.getText());
        if (!saved) {
            return;
        }

        // start or stop the timer according to the start button
        if (startRadioButton.isSelected()) {
            String notification = TimingService.openWorkTimer(timingClock);
            timeTipLabel.setText(notification);
            Notifier.notifyInfo(notification);
            JOptionPane.showMessageDialog(null, notification);
        } else {
            TimingService.closeWorkTimer();
        }
    }

    public JPanel getComponent() {
        return mainPanel;
    }
}
