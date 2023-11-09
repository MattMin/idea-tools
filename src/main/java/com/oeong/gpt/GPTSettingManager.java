package com.oeong.gpt;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;

import javax.swing.*;

public class GPTSettingManager {

    private Project project;
    private JPanel container;

    protected GPTSettingManager(Project project) {
        this.project = project;
    }

    public static GPTSettingManager getInstance(Project project) {
        return project.getService(GPTSettingManager.class);
    }

    public ActionToolbar createGPTToolbar(JPanel container) {
        this.container = container;

        // 工具栏
        DefaultActionGroup actions = new DefaultActionGroup();
        // 增加key
        actions.add(createSettingAction());
        actions.addSeparator();

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("ToolwindowToolbar", actions, true);
        actionToolbar.setTargetComponent(container);
        actionToolbar.adjustTheSameSize(true);
        return actionToolbar;
    }

    public GPTSettingAction createSettingAction() {
        GPTSettingAction settingAction = new GPTSettingAction();
        settingAction.setAction(e -> {
            // 弹出连接配置窗口
            GPTSettingDialog apiKeySettingsDialog = new GPTSettingDialog(project, this);
            apiKeySettingsDialog.show();
        });
        return settingAction;
    }

}
