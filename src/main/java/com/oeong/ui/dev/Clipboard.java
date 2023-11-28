package com.oeong.ui.dev;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.ui.components.JBScrollPane;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/11/22 9:36
 */
public class Clipboard {
    @Getter
    private JPanel container;
    private Project project;

    public static final Key<Object> CLIPBOARD = Key.create("Clipboard");

    public Clipboard(Project project) {
        this.project = project;
        initUI();
    }

    private void initUI() {
        container = new JPanel(new BorderLayout());

        ClipboardGroupComponent clipboardGroupComponent = new ClipboardGroupComponent();
        JBScrollPane jPanel = new JBScrollPane();
        jPanel.setViewportView(clipboardGroupComponent);
        jPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jPanel.updateUI();
        container.add(jPanel, BorderLayout.CENTER);

        // 临时保存数据
        project.putUserData(CLIPBOARD, clipboardGroupComponent);
    }
}
