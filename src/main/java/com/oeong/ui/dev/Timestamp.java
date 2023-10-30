package com.oeong.ui.dev;

import com.intellij.icons.AllIcons;
import com.intellij.ui.JBColor;
import com.oeong.notice.Notifier;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class Timestamp {
    private JComboBox<String> zoneComboBox;
    private JTextField dateTextField;
    private JButton transferButton1;
    private JTextField timestampTextField1;
    private JButton transferButton2;
    private JRadioButton secondRadioButton;
    private JPanel mainPanel;
    private JLabel currentTimestamp;
    private JLabel timestampLabel;
    private JLabel dateLabel;
    private JRadioButton millisecondRadioButton;
    private JButton currenCopyButton;
    private JButton timestampCopyButton;
    private JButton dateCopyButton;

    public static long currentTimestampSecond = 0;

    public Timestamp() {
        Timer currentTimer = new Timer(1000, Timestamp -> {
            currentTimestampSecond = secondRadioButton.isSelected()
                    ? System.currentTimeMillis() / 1000 : System.currentTimeMillis();
            currentTimestamp.setText(String.valueOf(currentTimestampSecond));
        });
        currentTimer.start();

        initZone();
        initUnit();
        updateAll();
        initCopyButton();

        // set the unit in the second/millisecond
        secondRadioButton.addActionListener(e -> {
            millisecondRadioButton.setSelected(false);
            updateAll();
        });

        millisecondRadioButton.addActionListener(e -> {
            secondRadioButton.setSelected(false);
            updateAll();
        });

        // set zone
        zoneComboBox.addActionListener(e -> {
            // get the selected item
            String selectedZone = (String) zoneComboBox.getSelectedItem();
            // set the selected item to the text field
            zoneComboBox.getEditor().setItem(selectedZone);

            updateAll();
        });

        // transfer the date to the timestamp
        transferButton1.addActionListener(e -> dateToTimestamp());

        // transfer the timestamp to the date
        transferButton2.addActionListener(e -> timestampToDate());

        // copy the current timestamp
        currentTimestamp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // one click
                if(e.getClickCount() == 1) {
                    copyCurrentTimestamp();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentTimestamp.setForeground(JBColor.BLACK);
            }
        });

        // copy the timestamp
        timestampLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // one click
                if(e.getClickCount() == 1) {
                    copyTimestampLabel();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                timestampLabel.setForeground(JBColor.BLACK);
            }
        });

        // copy the date
        dateLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // one click
                if(e.getClickCount() == 1) {
                    copyDateLabel();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                dateLabel.setForeground(JBColor.BLACK);
            }
        });
        currentTimestamp.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
            }
        });
        
        // copy button
        currenCopyButton.addActionListener(e -> {
            copyCurrentTimestamp();
            currentTimestamp.setForeground(JBColor.BLACK);
        });
        dateCopyButton.addActionListener(e -> {
            copyDateLabel();
            dateLabel.setForeground(JBColor.BLACK);
        });
        timestampCopyButton.addActionListener(e -> {
            copyTimestampLabel();
            timestampLabel.setForeground(JBColor.BLACK);
        });
    }

    private void initCopyButton() {
        currenCopyButton.setText("");
        currenCopyButton.setContentAreaFilled(false);
        currenCopyButton.setIcon(AllIcons.Actions.Copy);
        currenCopyButton.setBorder(BorderFactory.createEmptyBorder());

        dateCopyButton.setText("");
        dateCopyButton.setContentAreaFilled(false);
        dateCopyButton.setIcon(AllIcons.Actions.Copy);
        dateCopyButton.setBorder(BorderFactory.createEmptyBorder());

        timestampCopyButton.setText("");
        timestampCopyButton.setContentAreaFilled(false);
        timestampCopyButton.setIcon(AllIcons.Actions.Copy);
        timestampCopyButton.setBorder(BorderFactory.createEmptyBorder());
    }

    private void copyDateLabel() {
        String clipboardStr = dateLabel.getText();
        setClipboardString(clipboardStr);
        dateLabel.setForeground(JBColor.BLUE);
        Notifier.notifyInfo(clipboardStr + " has been copied to the clipboard.");
    }

    private void copyTimestampLabel() {
        String clipboardStr = timestampLabel.getText();
        setClipboardString(clipboardStr);
        timestampLabel.setForeground(JBColor.BLUE);
        Notifier.notifyInfo(clipboardStr + " has been copied to the clipboard.");
    }

    private void copyCurrentTimestamp() {
        String clipboardStr = currentTimestamp.getText();
        setClipboardString(clipboardStr);
        currentTimestamp.setForeground(JBColor.BLUE);
        Notifier.notifyInfo(clipboardStr + " has been copied to the clipboard.");
    }

    private void updateAll() {
        getCurrentTime();
        dateToTimestamp();
        timestampToDate();
    }

    private void timestampToDate() {
        try {
            String timestamp = timestampTextField1.getText();
            String zone = (String) zoneComboBox.getSelectedItem();

            // 解析时间戳字符串
            long ts = Long.parseLong(timestamp);
            ts = secondRadioButton.isSelected() ? ts * 1000 : ts;
            // 解析时区字符串
            ZoneId zoneId = ZoneId.of(zone.split(" ")[0]);
            // 将时间戳转换为Instant，然后获取LocalDateTime
            LocalDateTime dateTime = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(ts), zoneId);
            // 格式化日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            String date = dateTime.format(formatter);

            // set the date to the text field
            dateLabel.setText(date);
        } catch (Exception e) {
            Notifier.notifyError("Please enter the correct timestamp format.");
        }
    }

    private void dateToTimestamp() {
        try {
            String date = dateTextField.getText();
            String zone = (String) zoneComboBox.getSelectedItem();

            // 解析日期字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            // 解析时区字符串
            ZoneId zoneId = ZoneId.of(zone.split(" ")[0]);
            // 将LocalDateTime转换为Instant，然后获取时间戳（以毫秒为单位）
            long timestamp = dateTime.atZone(zoneId).toInstant().toEpochMilli();
            timestamp = secondRadioButton.isSelected() ? timestamp / 1000 : timestamp;

            // set the timestamp to the text field
            timestampLabel.setText(String.valueOf(timestamp));
        } catch (Exception e) {
            Notifier.notifyError("Please enter the correct date format. For example: 2023/01/01 00:00:00");
        }
    }

    private void getCurrentTime() {
        // get the current time
        currentTimestampSecond = secondRadioButton.isSelected() ? System.currentTimeMillis() / 1000 : System.currentTimeMillis();
        // set the time to the text field
        currentTimestamp.setText(String.valueOf(currentTimestampSecond));
    }

    void initZone() {

        Set<String> timeZones = ZoneId.getAvailableZoneIds();
        Set<String> timeUTCZones = new HashSet<>();

        for (String timeZone : timeZones) {
            ZoneId zoneId = ZoneId.of(timeZone);
            int offset = zoneId.getRules().getOffset(java.time.Instant.now()).getTotalSeconds() / 3600;

            if (offset >= -12 && offset <= 13) {
                String sign = offset >= 0 ? "+" : "-";
                String utc = " (UTC" + sign + String.format("%02d:00", Math.abs(offset)) + ")";
                timeUTCZones.add(timeZone + utc);
            }
        }

        zoneComboBox.setModel(new DefaultComboBoxModel<>(timeUTCZones.toArray(new String[0])));
        zoneComboBox.setSelectedItem("Asia/Shanghai (UTC+08:00)");
    }

    void initUnit() {
        secondRadioButton.setSelected(true);
        millisecondRadioButton.setSelected(false);
    }

    /**
     * 设置剪贴板内容(复制)
     */
    public static void setClipboardString(String str) {
        //获取协同剪贴板，单例
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //封装文本内容
        Transferable trans = new StringSelection(str);
        //把文本内容设置到系统剪贴板上
        clipboard.setContents(trans, null);
    }

    public JPanel getComponent() {
        return mainPanel;
    }
}
