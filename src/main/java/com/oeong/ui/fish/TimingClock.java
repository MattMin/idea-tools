package com.oeong.ui.fish;

import javax.swing.*;


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

        // status button
        startRadioButton.addActionListener(e -> {
            if (startRadioButton.isSelected()) {
                stopRadioButton.setSelected(false);
                startRadioButton.setText("Running");
                tipLabel.setText(startTip);

            }
        });
        stopRadioButton.addActionListener(e -> {
            if (stopRadioButton.isSelected()) {
                startRadioButton.setSelected(false);
                startRadioButton.setText("Start");
                tipLabel.setText(stopTip);
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
