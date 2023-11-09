package com.oeong.gpt;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.oeong.gpt.core.GPTCoreAction;
import org.jetbrains.annotations.NotNull;

import static com.oeong.gpt.core.GPTCoreAction.BUG;
import static com.oeong.gpt.core.GPTCoreAction.BUG_PROMPT;

public class FindBugAction extends AnAction {

    public FindBugAction() {
        super(BUG);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        GPTCoreAction coreAction = new GPTCoreAction();
        coreAction.doActionPerformed(e, BUG_PROMPT);
    }
}
