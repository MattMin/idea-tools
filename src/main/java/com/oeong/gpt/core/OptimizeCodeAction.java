package com.oeong.gpt.core;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import static com.oeong.gpt.core.OpenAISettingsState.OPTIMIZE;
import static com.oeong.gpt.core.OpenAISettingsState.OPTIMIZE_PROMPT;

public class OptimizeCodeAction extends AnAction {
    public OptimizeCodeAction() {
        super(OPTIMIZE);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new SendAction().doPromptActionPerformed(e, OPTIMIZE_PROMPT);
    }
}
