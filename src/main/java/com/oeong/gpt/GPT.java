package com.oeong.gpt;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.oeong.gpt.ui.MainPanel;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class GPT {

    Project project;

    @Getter
    private JPanel container;
    public static final Key<Object> ACTIVE_CONTENT = Key.create("ActiveContent");

    public GPT(Project project) {
        this.project = project;
        initUI();
    }

    private void initUI() {
        container = new JPanel(new BorderLayout());

        // 添加主面板
        MainPanel mainPanel = new MainPanel(project);
        container.add(mainPanel.init(), BorderLayout.CENTER);

        // 临时保存数据
        project.putUserData(ACTIVE_CONTENT, mainPanel);
    }
}
