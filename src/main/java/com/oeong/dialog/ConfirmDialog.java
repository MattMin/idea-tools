package com.oeong.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

/**
 * @author mzyupc@163.com
 * @date 2021/8/7 5:33 下午
 *
 * 确认提醒窗口
 */
public class ConfirmDialog extends DialogWrapper {
    private final String centerPanelText;
    private Consumer<ActionEvent> customOkFunction;
    private Project project;

    /**
     * @param project
     * @param title            对话框标题
     * @param centerPanelText  要提示的内容
     * @param customOkFunction 自定义的ok按钮功能
     */
    public ConfirmDialog(@NotNull Project project, String title, String centerPanelText, Consumer<ActionEvent> customOkFunction) {
        super(project);
        this.project = project;
        this.centerPanelText = centerPanelText;
        this.customOkFunction = customOkFunction;
        this.setTitle(title);
        this.setResizable(false);
        this.setAutoAdjustable(true);
        this.myOKAction = new CustomOKAction();
        this.init();
    }

    public void addCustomOkFunction(Consumer<ActionEvent> customOkFunction) {
        this.customOkFunction = customOkFunction;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected @Nullable
    JComponent createCenterPanel() {
        JLabel jLabel = new JLabel(centerPanelText);
        jLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        return jLabel;
    }

    /**
     * 自定义 ok Action
     */
    protected class CustomOKAction extends DialogWrapperAction {
        protected CustomOKAction() {
            super("OK");
            putValue(DialogWrapper.DEFAULT_ACTION, true);
        }

        @Override
        protected void doAction(ActionEvent e) {
            if (customOkFunction != null) {
                customOkFunction.accept(e);
            }
            close(OK_EXIT_CODE);
        }
    }
}

