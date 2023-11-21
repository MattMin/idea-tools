package com.oeong.ui.dev;

import com.oeong.notice.Notifier;
import lombok.Getter;

import javax.swing.*;

import static com.oeong.service.TimingService.isInteger;

public class UUID {
    @Getter
    private JPanel container;
    private JTextField numTextField;
    private JRadioButton uppercaseRadioButton;
    private JRadioButton lowercaseRadioButton;
    private JCheckBox removeTheDashOfCheckBox;
    private JButton generateButton;
    private JScrollPane scrollPane;

    public UUID() {

        // radio button
        ButtonGroup group = new ButtonGroup();
        group.add(uppercaseRadioButton);
        group.add(lowercaseRadioButton);

        // generate button
        generateButton.addActionListener(e -> onOK());
    }

    public void onOK() {
        String num = numTextField.getText();
        if (num == null || num.isEmpty()) {
            Notifier.notifyError("Please enter the number of UUIDs to be generated");
            return;
        }

        if (!isInteger(num)) {
            Notifier.notifyError("Please enter an integer");
            return;
        }

        int numInt = Integer.parseInt(num);
        if (numInt <= 0 || numInt > 200) {
            Notifier.notifyError("Please enter an integer between 1 and 200");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numInt; i++) {
            String uuid = java.util.UUID.randomUUID().toString();
            if (removeTheDashOfCheckBox.isSelected()) {
                uuid = uuid.replaceAll("-", "");
            }
            if (uppercaseRadioButton.isSelected()) {
                uuid = uuid.toUpperCase();
            } else {
                uuid = uuid.toLowerCase();
            }
            sb.append(uuid).append("\n");
        }
        scrollPane.setViewportView(new JTextArea(sb.toString()));
    }
}
