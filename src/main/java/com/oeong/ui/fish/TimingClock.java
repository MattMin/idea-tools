package com.oeong.ui.fish;

import com.intellij.openapi.util.text.StringUtil;
import com.oeong.data.TimingCenter;
import com.oeong.notice.Notifier;
import com.oeong.service.TimingService;

import javax.swing.*;

import static com.oeong.data.TimingCenter.RUNNING_TIP;
import static com.oeong.data.TimingCenter.STOPPED_TIP;
import static com.oeong.service.TimingService.isInteger;

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

        // radio button
        ButtonGroup group = new ButtonGroup();
        group.add(startRadioButton);
        group.add(stopRadioButton);

        // status button
        startRadioButton.addActionListener(e -> {
            if  (!validateTime()) {
                chooseStopButton();
                return;
            }

            if (TimingCenter.status.equals(TimingCenter.CLOSE)) {
                chooseStartButton();
                handle(this);
            }
        });
        stopRadioButton.addActionListener(e -> {
            if (!validateTime()) {
                chooseStopButton();
                return;
            }

            if (!TimingCenter.status.equals(TimingCenter.CLOSE)) {
                chooseStopButton();
                handle(this);
            }
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
        TimingService.saveSetting(restField.getText(), workField.getText());

        // start or stop the timer according to the start button
        if (startRadioButton.isSelected()) {
            String notification = TimingService.openWorkTimer(timingClock);
            timeTipLabel.setText(notification);
            Notifier.notifyInfo(notification);
            Object[] options = {"OK"};
            JOptionPane.showOptionDialog(null, notification, "Timing Clock",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        } else {
            TimingService.closeWorkTimer();
        }
    }

    public boolean validateTime() {
        String workTime = workField.getText();
        String restTime = restField.getText();

        if (StringUtil.isNotEmpty(workTime) && isInteger(workTime)) {
            int work = Integer.parseInt(workTime);
            if (work < 1 || work > 120) {
                Notifier.notifyError("The work time must be between 1 and 120 minutes.");
                return false;
            }
        } else {
            Notifier.notifyError("The work time must be an integer.");
            return false;
        }

        if (StringUtil.isNotEmpty(restTime) && isInteger(restTime)) {
            int rest = Integer.parseInt(restTime);
            if  (rest < 1 || rest > 60) {
                Notifier.notifyError("The rest time must be between 1 and 60 minutes.");
                return false;
            }
        } else {
            Notifier.notifyError("The rest time must be an integer.");
            return false;
        }

        return true;
    }

    public JPanel getComponent() {
        return mainPanel;
    }
}
