package com.oeong.gpt;


import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.oeong.gpt.ui.MessageGroupComponent;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class GPT {

    Project project;

    @Getter
    private JPanel container;
    private JPanel gptPanel;
    private JPanel toolPanel;
    GPTSettingManager gptSettingManager;
    public static final Key ACTIVE_CONTENT = Key.create("ActiveContent");
    public static final Key ACTIVE_GPTPANEL = Key.create("ActiveGPTPanel");
    public static final Key ACTIVE_COMPONENT = Key.create("ActiveComponent");

    public GPT(Project project) {
        this.project = project;
        initUI();
    }

    private void initUI() {
        container = new JPanel(new BorderLayout());
        toolPanel = new JPanel(new BorderLayout());
        gptPanel = new JPanel(new BorderLayout());

        // 添加工具栏
        gptSettingManager = GPTSettingManager.getInstance(project);
        ActionToolbar gptToolbar = gptSettingManager.createGPTToolbar(toolPanel);
        toolPanel.add(gptToolbar.getComponent(), BorderLayout.NORTH);
        container.add(toolPanel, BorderLayout.NORTH);

        // 添加主面板
        MessageGroupComponent messageGroupComponent = new MessageGroupComponent();
        gptPanel.add(messageGroupComponent, BorderLayout.CENTER);
        container.add(gptPanel, BorderLayout.CENTER);

        // 临时保存数据
        project.putUserData(ACTIVE_CONTENT, container);
        project.putUserData(ACTIVE_GPTPANEL, gptPanel);
        project.putUserData(ACTIVE_COMPONENT, messageGroupComponent);
    }
}
