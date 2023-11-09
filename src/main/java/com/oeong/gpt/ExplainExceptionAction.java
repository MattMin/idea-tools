package com.oeong.gpt;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.oeong.gpt.core.GPTCoreAction;
import org.jetbrains.annotations.NotNull;

import static com.oeong.gpt.core.GPTCoreAction.EXCEPTION_PROMPT;
import static com.oeong.gpt.core.GPTCoreAction.EXPLAIN;

public class ExplainExceptionAction extends AnAction {

    public ExplainExceptionAction() {
        super(EXPLAIN);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        GPTCoreAction coreAction = new GPTCoreAction();
        coreAction.doActionPerformed(e, EXCEPTION_PROMPT);
    }
}
