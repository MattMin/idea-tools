package com.oeong.gpt.core;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import static com.oeong.gpt.core.OpenAISettingsState.EXCEPTION;
import static com.oeong.gpt.core.OpenAISettingsState.EXCEPTION_PROMPT;

public class ExplainExceptionAction extends AnAction {

    public ExplainExceptionAction() {
        super(EXCEPTION);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new SendAction().doPromptActionPerformed(e, EXCEPTION_PROMPT);
    }
}
