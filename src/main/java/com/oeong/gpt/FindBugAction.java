package com.oeong.gpt;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.oeong.gpt.core.SendAction;
import com.oeong.gpt.ui.MainPanel;
import org.jetbrains.annotations.NotNull;

import static com.oeong.gpt.GPT.ACTIVE_COMPONENT;
import static com.oeong.gpt.core.GPTCoreAction.BUG;
import static com.oeong.gpt.core.GPTCoreAction.BUG_PROMPT;

public class FindBugAction extends AnAction {

    public FindBugAction() {
        super(BUG);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        MainPanel mainPanel = (MainPanel)project.getUserData(ACTIVE_COMPONENT);

        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert editor != null;
        String selectedText = editor.getSelectionModel().getSelectedText();
        String question = BUG_PROMPT + selectedText;

        SendAction sendAction = mainPanel.getProject().getService(SendAction.class);
        sendAction.doActionPerformed(mainPanel, question);
    }
}
