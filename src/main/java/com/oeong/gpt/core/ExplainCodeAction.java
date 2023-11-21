package com.oeong.gpt.core;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import static com.oeong.gpt.core.OpenAISettingsState.EXPLAIN;
import static com.oeong.gpt.core.OpenAISettingsState.EXPLAIN_PROMPT;

public class ExplainCodeAction extends AnAction {
    public ExplainCodeAction() {
        super(EXPLAIN);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new SendAction().doPromptActionPerformed(e, EXPLAIN_PROMPT);
    }
}
