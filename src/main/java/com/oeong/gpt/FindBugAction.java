package com.oeong.gpt;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.oeong.gpt.core.SendAction;
import com.oeong.gpt.ui.MainPanel;
import org.jetbrains.annotations.NotNull;

import static com.oeong.gpt.GPT.ACTIVE_CONTENT;
import static com.oeong.gpt.OpenAISettingsState.BUG;
import static com.oeong.gpt.OpenAISettingsState.BUG_PROMPT;

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
        MainPanel mainPanel = (MainPanel) project.getUserData(ACTIVE_CONTENT);

        SendAction sendAction = mainPanel.getProject().getService(SendAction.class);
        sendAction.doPromptActionPerformed(mainPanel, BUG_PROMPT, "```" + selectedText + "```");
    }
}
