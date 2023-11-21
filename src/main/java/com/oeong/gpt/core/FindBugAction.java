package com.oeong.gpt.core;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import static com.oeong.gpt.core.OpenAISettingsState.BUG;
import static com.oeong.gpt.core.OpenAISettingsState.BUG_PROMPT;

public class FindBugAction extends AnAction {

    public FindBugAction() {
        super(BUG);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new SendAction().doPromptActionPerformed(e, BUG_PROMPT);
    }
}
