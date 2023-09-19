package com.oeong.ui;

import com.intellij.ui.JBColor;
import com.oeong.notificationGroup.Notifier;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class Timestamp {
    private JComboBox<String> zoneComboBox;
    private JButton getButton;
    private JTextField dateTextField;
    private JButton transferButton1;
    private JTextField timestampTextField1;
    private JButton transferButton2;
    private JRadioButton secondRadioButton;
    private JPanel mainPanel;
    private JLabel currentTimestamp;
    private JLabel timestampLabel;
    private JLabel dateLabel;

    public static long currentTimestampSecond = 0;

    public Timestamp() {
        initZone();
        initUnit();
        initAll();
        Timer currentTimer = new Timer(1000, Timestamp -> {
            currentTimestampSecond = secondRadioButton.isSelected() ? System.currentTimeMillis() : System.currentTimeMillis() / 1000;
            currentTimestamp.setText(String.valueOf(currentTimestampSecond));
        });
        currentTimer.start();

        // set the unit in the second/millisecond
        secondRadioButton.addActionListener(e -> {
            if (secondRadioButton.isSelected()) {
                secondRadioButton.setText("millisecond");
            } else {
                secondRadioButton.setText("second     ");
            }
            initAll();
        });

        // set zone
        zoneComboBox.addActionListener(e -> {
            // get the selected item
            String selectedZone = (String) zoneComboBox.getSelectedItem();
            // set the selected item to the text field
            zoneComboBox.getEditor().setItem(selectedZone);

            initAll();
        });

        // get the current time
        getButton.addActionListener(e -> {
            getCurrentTime();
            copyCurrentTimestamp();
            currentTimestamp.setForeground(JBColor.BLACK);
        });

        // transfer the date to the timestamp
        transferButton1.addActionListener(e -> {
            dateToTimestamp();
            copyTimestampLabel();
            timestampLabel.setForeground(JBColor.BLACK);
        });

        // transfer the timestamp to the date
        transferButton2.addActionListener(e -> {
            timestampToDate();
            copyDateLabel();
            dateLabel.setForeground(JBColor.BLACK);
        });

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

    private void initAll() {
        getCurrentTime();
        dateToTimestamp();
        timestampToDate();
    }

    private void timestampToDate() {
        try {
            String timestamp = timestampTextField1.getText();
            String zone = (String) zoneComboBox.getSelectedItem();
            assert zone != null;

            // 解析时间戳字符串
            long ts = Long.parseLong(timestamp);
            ts = secondRadioButton.isSelected() ? ts : ts * 1000;
            // 解析时区字符串
            ZoneId zoneId = ZoneId.of(zone);
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
            assert zone != null;

            // 解析日期字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            // 解析时区字符串
            ZoneId zoneId = ZoneId.of(zone);
            // 将LocalDateTime转换为Instant，然后获取时间戳（以毫秒为单位）
            long timestamp = dateTime.atZone(zoneId).toInstant().toEpochMilli();
            timestamp = secondRadioButton.isSelected() ? timestamp : timestamp / 1000;

            // set the timestamp to the text field
            timestampLabel.setText(String.valueOf(timestamp));
        } catch (Exception e) {
            Notifier.notifyError("Please enter the correct date format. For example: 2023/01/01 00:00:00");
        }
    }

    private void getCurrentTime() {
        // get the current time
        currentTimestampSecond = secondRadioButton.isSelected() ? System.currentTimeMillis() : System.currentTimeMillis() / 1000;
        // set the time to the text field
        currentTimestamp.setText(String.valueOf(currentTimestampSecond));
    }

    void initZone() {
        // get the zone list
        String[] zoneList = TimeZone.getAvailableIDs();
        // add the zone list to the combo box
        zoneComboBox.setModel(new DefaultComboBoxModel<>(zoneList));
        zoneComboBox.getEditor().setItem("Asia/Shanghai");
    }

    void initUnit() {
        secondRadioButton.setSelected(true);
        secondRadioButton.setText("millisecond");
    }

    /**
     * 获取剪贴板内容(粘贴)
     */
    public String getClipboardString() {
        //获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //获取剪贴板内容
        Transferable trans = clipboard.getContents(null);
        if(trans != null) {
            //判断剪贴板内容是否支持文本
            if(trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String clipboardStr = null;
                try {
                    //获取剪贴板的文本内容
                    clipboardStr = (String) trans.getTransferData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
                return clipboardStr;
            }
        }
        return null;
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
