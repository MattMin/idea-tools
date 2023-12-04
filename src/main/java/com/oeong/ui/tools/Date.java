package com.oeong.ui.tools;

import com.oeong.notice.Notifier;
import lombok.Getter;

import javax.swing.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Date {
    private JTextField dateTextField1;
    private JButton setTodayButton1;
    private JButton setTodayButton2;
    private JButton calculateButton1;
    private JTextField answerTextField1;
    private JTextField dateTextField3;
    private JButton setTodayButton3;
    private JComboBox compareComboBox;
    private JTextField timeTextField;
    private JComboBox unitComboBox;
    private JButton calculateButton2;
    private JTextField answerTextField2;
    private JTextField dateTextField2;
    @Getter
    private JPanel container;

    public Date() {
        dateTextField1.setText(getToday());
        dateTextField2.setText(getToday());
        dateTextField3.setText(getToday());

        answerTextField1.setEditable(false);
        answerTextField2.setEditable(false);

        setTodayButton1.addActionListener(e -> dateTextField1.setText(getToday()));
        setTodayButton2.addActionListener(e -> dateTextField2.setText(getToday()));
        setTodayButton3.addActionListener(e -> dateTextField3.setText(getToday()));

        calculateButton1.addActionListener(e -> onCalculate1());
        calculateButton2.addActionListener(e -> onCalculate2());
    }

    public String getToday() {
        LocalDate localDate = LocalDate.now();
        return localDate.toString();
    }

    public String calculateProjectedDate(LocalDate date, int time, String compare, String unit) {

        if (compare.equals("Before")) {
            time = -time;
        }

        ChronoUnit chronoUnit = switch (unit) {
            case "Days" -> ChronoUnit.DAYS;
            case "Weeks" -> ChronoUnit.WEEKS;
            case "Months" -> ChronoUnit.MONTHS;
            default -> null;
        };

        assert chronoUnit != null;
        return date.plus(time, chronoUnit).toString();
    }

    public void onCalculate1() {
        String date1 = dateTextField1.getText();
        String date2 = dateTextField2.getText();

        LocalDate localDate1;
        LocalDate localDate2;

        try {
            localDate1 = LocalDate.parse(date1);
        } catch (Exception exception) {
            Notifier.notifyError("Start date error. Correct format: yyyy-MM-dd");
            return;
        }

        try {
            localDate2 = LocalDate.parse(date2);
        } catch (Exception exception) {
            Notifier.notifyError("End date error. Correct format: yyyy-MM-dd");
            return;
        }

        long daysDifference = ChronoUnit.DAYS.between(localDate1, localDate2);

        answerTextField1.setText(String.valueOf(daysDifference));
    }
    public void onCalculate2() {
        String date = dateTextField3.getText();
        String time = timeTextField.getText();
        String compare = Objects.requireNonNull(compareComboBox.getSelectedItem()).toString();
        String unit = Objects.requireNonNull(unitComboBox.getSelectedItem()).toString();

        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date);
        } catch (Exception exception) {
            Notifier.notifyError("Date error. Correct format: yyyy-MM-dd");
            return;
        }

        int timeInt;
        try {
            timeInt = Integer.parseInt(time);
        } catch (Exception exception) {
            Notifier.notifyError("Time error. You must enter an integer");
            return;
        }

        answerTextField2.setText(calculateProjectedDate(localDate, timeInt, compare, unit));
    }
}
