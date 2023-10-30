package com.oeong.action;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * @author mzyupc@163.com
 */
public abstract class CustomAction extends AnAction {
    protected Consumer<AnActionEvent> action;
    String actionId;

    public CustomAction(String actionId,String text, String description, Icon icon) {
        super(text, description, icon);
        ActionManager actionManager = ActionManager.getInstance();
        actionManager.registerAction(actionId, this, PluginId.findId("com.oeong.idea-tools"));
        this.actionId = actionId;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        if (this.action == null) {
            throw new RuntimeException("action not set");
        }
        action.accept(anActionEvent);
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }

    public void setAction(Consumer<AnActionEvent> action) {
        this.action = action;
    }


    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }
}
