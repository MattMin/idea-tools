package com.oeong.gpt.core;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.oeong.notice.Notifier;
import com.oeong.ui.ai.MainPanel;
import org.jetbrains.annotations.NotNull;

import static com.oeong.gpt.core.OpenAISettingsState.BUG;
import static com.oeong.gpt.core.OpenAISettingsState.BUG_PROMPT;
import static com.oeong.ui.ai.GPT.ACTIVE_CONTENT;

public class FindBugAction extends AnAction {

    public FindBugAction() {
        super(BUG);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // get selected text
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert editor != null;
        String selectedText = editor.getSelectionModel().getSelectedText();

        // get main panel
        Project project = e.getProject();
        assert project != null;
        MainPanel mainPanel = (MainPanel) project.getUserData(ACTIVE_CONTENT);

        if (mainPanel == null) {
            Notifier.notifyWarn("Please open the GPT window first.");
            return;
        }
        SendAction sendAction = mainPanel.getProject().getService(SendAction.class);
        sendAction.doPromptActionPerformed(mainPanel, BUG_PROMPT, "```" + selectedText + "```");
    }
}
