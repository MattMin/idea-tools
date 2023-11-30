package com.oeong.ui.tools;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.ui.components.JBScrollPane;
import com.oeong.ui.tools.draftCalculator.DraftCalculatorGroupComponent;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class DraftCalculator {

    @Getter
    private JPanel container;
    Project project;
    public static final Key<Object> CALCULATOR_JBScroll = Key.create("CalculatorJBScroll");
    public static final Key<Object> CALCULATOR_CONTENT = Key.create("CalculatorContent");

    public DraftCalculator(Project project) {
        this.project = project;
        initUI();
    }

    private void initUI() {
        container = new JPanel(new BorderLayout());

        JBScrollPane jbScrollPane = new JBScrollPane();
        DraftCalculatorGroupComponent draftCalculatorGroupComponent = new DraftCalculatorGroupComponent(project);
        jbScrollPane.setViewportView(draftCalculatorGroupComponent);
        jbScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jbScrollPane.updateUI();
        container.add(jbScrollPane, BorderLayout.CENTER);

        project.putUserData(CALCULATOR_JBScroll, jbScrollPane);
        project.putUserData(CALCULATOR_CONTENT, draftCalculatorGroupComponent);
    }
}
