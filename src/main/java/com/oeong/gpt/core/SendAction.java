package com.oeong.gpt.core;

import com.intellij.openapi.project.Project;
import com.oeong.gpt.GPT35TurboHandler;
import com.oeong.notice.Notifier;
import com.oeong.ui.ai.MainPanel;
import com.oeong.ui.ai.MessageComponent;
import com.oeong.ui.ai.MessageGroupComponent;
import okhttp3.Call;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class SendAction {

    private static final Logger LOG = LoggerFactory.getLogger(SendAction.class);

    public void doActionPerformed(MainPanel mainPanel, String data) {
        // Filter the empty text
        if (StringUtils.isEmpty(data)) {
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

    public void doPromptActionPerformed(MainPanel mainPanel, String prompt, String data) {

        if (StringUtils.isEmpty(data)) {
            Notifier.notifyWarn("Please select the text first.");
            return;
        }

        doActionPerformed(mainPanel, prompt + data);
    }
}
