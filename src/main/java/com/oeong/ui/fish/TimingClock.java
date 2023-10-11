package com.oeong.ui.fish;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

// TODO: 2023/10/11 初始化异步调用请求，休息时间结束后还可以休息，语气活泼一点，下面的文字大一点
public class TimingClock {
    private JRadioButton startRadioButton;
    private JRadioButton stopRadioButton;
    private JTextField workField;
    private JTextField restField;
    private JPanel mainPanel;
    private JLabel tipLabel;

    private static final String startTip = "The work is running.";
    private static final String stopTip = "The work hasn't started yet.";


    public TimingClock() {
        String workFieldText = workField.getText();
        String restFieldText = restField.getText();

        Timer timer = new Timer(true);
        WorkTimerTask workTimerTask = new WorkTimerTask();

        // status button
        startRadioButton.addActionListener(e -> {
            if (startRadioButton.isSelected()) {
                stopRadioButton.setSelected(false);
                startRadioButton.setText("Running");
                stopRadioButton.setText("Stop");
                tipLabel.setText(startTip);

                JOptionPane.showConfirmDialog(null, "Start to work, please.", "Tip", JOptionPane.DEFAULT_OPTION);

                long workMinutes = Long.parseLong(workFieldText);
                long restMinutes = Long.parseLong(restFieldText);
                timer.schedule(workTimerTask, restMinutes * 1000, workMinutes * 1000);
            }
        });
        stopRadioButton.addActionListener(e -> {
            if (stopRadioButton.isSelected()) {
                startRadioButton.setSelected(false);
                startRadioButton.setText("Start");
                stopRadioButton.setText("Stopped");
                tipLabel.setText(stopTip);

                JOptionPane.showConfirmDialog(null, "Timing clock is stopped.", "Tip", JOptionPane.DEFAULT_OPTION);
                timer.cancel();
            }
        });


    }

    void verify() {
        if (startRadioButton.isSelected()) {
            String workFieldText = workField.getText();
            String restFieldText = restField.getText();

            if (workFieldText == null || workFieldText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "请输入工作时间", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (restFieldText == null || restFieldText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "请输入休息时间", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int workMinutes = Integer.parseInt(workFieldText);
            int restMinutes = Integer.parseInt(restFieldText);

            if (workMinutes <= 0) {
                JOptionPane.showMessageDialog(null, "工作时间必须大于0", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (restMinutes <= 0) {
                JOptionPane.showMessageDialog(null, "休息时间必须大于0", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showConfirmDialog(null, "开始工作", "提示", JOptionPane.DEFAULT_OPTION);
        }

    }

    public JPanel getComponent() {
        return mainPanel;
    }
}

class WorkTimerTask extends TimerTask {
    @Override
    public void run() {
//        JOptionPane.showConfirmDialog(null, "Timing clock is running.", "Tip", JOptionPane.DEFAULT_OPTION);
        System.out.println("Timing clock is running." + System.currentTimeMillis() / 1000);
    }
}
