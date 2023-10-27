package com.oeong.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.oeong.dialog.OcrDialog;
import com.oeong.tools.ApiSettingManager;
import org.jetbrains.annotations.NotNull;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/10/19 10:34
 */
public class OcrAction extends CustomAction {

    public OcrAction() {
        super("com.oeong.action.OcrAction","Ocr", "Ocr", AllIcons.Actions.GroupByFile);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        if (this.action == null) {
            throw new RuntimeException("action not set");
        }
        Project project = anActionEvent.getProject();
        this.setAction(e -> {
            // 弹出Ocr窗口
            ApiSettingManager apiSettingManager = project.getService(ApiSettingManager.class);
            OcrDialog ocrDialog = new OcrDialog(project, apiSettingManager);
            ocrDialog.show();
        });
        action.accept(anActionEvent);
    }

    /**
     * Updates the presentation of the action just before the {@link #actionPerformed(AnActionEvent)} method is called.
     * The default implementation simply delegates to the {@link #update(AnActionEvent)} method.
     * <p/>
     * It is called on the UI thread with all data in the provided {@link DataContext} instance.
     *
     * @param e
     * @see #actionPerformed(AnActionEvent)
     */
    @Override
    public void beforeActionPerformedUpdate(@NotNull AnActionEvent e) {
        super.beforeActionPerformedUpdate(e);
    }
}
