package com.oeong.ui.tools.draftCalculator;

import cn.hutool.core.swing.clipboard.ClipboardUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.oeong.notice.Notifier;
import org.mariuszgromada.math.mxparser.Expression;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.oeong.ui.tools.DraftCalculator.CALCULATOR_CONTENT;
import static com.oeong.ui.tools.DraftCalculator.CALCULATOR_JBScroll;
import static com.oeong.ui.tools.draftCalculator.DraftCalculatorGroupComponent.componentMap;

public class DraftCalculatorComponent extends JBPanel<DraftCalculatorComponent> {

    private String answer;
    private final JTextField inputField;
    private final JLabel resultLabel;

    public DraftCalculatorComponent(Integer index, String content, boolean me, Project project) {
        answer = content;
        setDoubleBuffered(true);
        setOpaque(true);
        setBackground(me ? new JBColor(0xEAEEF7, 0x45494A) : new JBColor(0xE0EEF7, 0x2d2f30 /*2d2f30*/));
        setBorder(JBUI.Borders.empty(10, 10, 10, 0));
        setLayout(new BorderLayout(JBUI.scale(10), 0));

        // input
        inputField = new JTextField(content);
        inputField.addActionListener(e -> calculateResult(index, project));
        add(inputField, BorderLayout.NORTH);

        // result
        resultLabel = new JLabel("= " + answer);
        add(resultLabel, BorderLayout.SOUTH);

        // copy
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        actionPanel.setBorder(JBUI.Borders.emptyRight(10));
        JLabel copyAction = new JLabel(AllIcons.Actions.Copy);
        copyAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        copyAction.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            ClipboardUtil.setStr(answer);
            Notifier.notifyInfo("Copy Success!\n" + answer);
            }
        });
        actionPanel.add(copyAction, BorderLayout.NORTH);
        add(actionPanel, BorderLayout.EAST);

        // componentMap
        if (index == 0) {
            componentMap.clear();
        }
        componentMap.put(index, this);
    }

    private void calculateResult(Integer index, Project project) {
        String inputText = inputField.getText();

        Expression expression = new Expression(inputText);
        String result = String.valueOf(expression.calculate());
        if (result.endsWith(".0")) {
            result = result.substring(0, result.length() - 2);
        }

        resultLabel.setText("= " + result);
        answer = result;

        // 判断下一个组件是否存在，存在则更新，不存在则创建
        Integer newIndex = index + 1;
        if (componentMap.containsKey(newIndex)) {
            componentMap.get(newIndex).inputField.setText(result);
            componentMap.get(newIndex).resultLabel.setText("= " + result);
        } else {
            DraftCalculatorGroupComponent draftCalculatorGroupComponent = (DraftCalculatorGroupComponent) project.getUserData(CALCULATOR_CONTENT);
            assert draftCalculatorGroupComponent != null;
            draftCalculatorGroupComponent.add(new DraftCalculatorComponent(newIndex, result, false, project));
        }
        // 将焦点移动到下一个组件
        componentMap.get(newIndex).inputField.requestFocusInWindow();
        this.scrollToBottom(project);
    }

    // 将JBScrollPane滚动到底部
    public void scrollToBottom(Project project) {
        JBScrollPane jbScrollPane = (JBScrollPane) project.getUserData(CALCULATOR_JBScroll);
        assert jbScrollPane != null;

        // 通过在 SwingUtilities.invokeLater 中设置滚动条的值，可以确保在 EDT（事件分发线程）上执行此操作，从而避免多线程问题。
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = jbScrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        });

        project.putUserData(CALCULATOR_JBScroll, jbScrollPane);
    }
}
