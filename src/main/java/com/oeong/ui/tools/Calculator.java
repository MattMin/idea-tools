package com.oeong.ui.tools;

import com.oeong.ui.tools.calculator.CalculatorKeyArea;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class Calculator {
    @Getter
    private JPanel container;
    public Calculator() {
        initUI();
    }

    private void initUI() {
        container = new JPanel(new BorderLayout());

        // calculator key area
        CalculatorKeyArea keyArea = new CalculatorKeyArea();
        container.add(keyArea, BorderLayout.NORTH);
    }
}
