package com.oeong.gpt.core;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.oeong.gpt.GPT35TurboHandler;
import com.oeong.notice.Notifier;
import com.oeong.ui.ai.MainPanel;
import com.oeong.ui.ai.MessageComponent;
import com.oeong.ui.ai.MessageGroupComponent;
import okhttp3.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

import static com.oeong.ui.ai.GPT.ACTIVE_CONTENT;

public class SendAction {

    private static final Logger LOG = LoggerFactory.getLogger(SendAction.class);

    public void doActionPerformed(MainPanel mainPanel, String data) {
        // Filter the empty text
        if (StringUtil.isEmpty(data)) {
            return;
        }

        // Reset the question container
        mainPanel.getSearchTextArea().getTextArea().setText("");
        mainPanel.aroundRequest(true);
        Project project = mainPanel.getProject();
        MessageGroupComponent contentPanel = mainPanel.getContentPanel();

        // Add the message component to container
        MessageComponent question = new MessageComponent(data, true);
        MessageComponent answer = new MessageComponent("Waiting for response...", false);
        contentPanel.add(question);
        contentPanel.add(answer);

        try {
            ExecutorService executorService = mainPanel.getExecutorService();
            // Request the server.
            GPT35TurboHandler gpt35TurboHandler = project.getService(GPT35TurboHandler.class);
            executorService.submit(() -> {
                Call handle = gpt35TurboHandler.handle(mainPanel, answer, data);
                mainPanel.setRequestHolder(handle);
                contentPanel.updateLayout();
                contentPanel.scrollToBottom();
            });
        } catch (Exception e) {
            answer.setSourceContent(e.getMessage());
            answer.setContent(e.getMessage());
            mainPanel.aroundRequest(false);
            LOG.error("ChatGPT: Request failed, error={}", e.getMessage());
        }
    }

    public void doPromptActionPerformed(AnActionEvent e, String prompt) {
        // get selected text
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert editor != null;
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (StringUtil.isEmpty(selectedText)) {
            Notifier.notifyWarn("Please select the text first.");
            return;
        }

        // get main panel
        Project project = e.getProject();
        assert project != null;
        MainPanel mainPanel = (MainPanel) project.getUserData(ACTIVE_CONTENT);
        if (mainPanel == null) {
            Notifier.notifyWarn("Please open the GPT window first.");
            return;
        }

        SendAction sendAction = mainPanel.getProject().getService(SendAction.class);
        sendAction.doActionPerformed(mainPanel, prompt + selectedText);
    }
}
