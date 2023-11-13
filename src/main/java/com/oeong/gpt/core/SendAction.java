package com.oeong.gpt.core;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.oeong.gpt.GPT35TurboHandler;
import com.oeong.gpt.ui.MainPanel;
import com.oeong.gpt.ui.MessageComponent;
import com.oeong.gpt.ui.MessageGroupComponent;
import okhttp3.Call;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

import static com.oeong.gpt.GPT.ACTIVE_CONTENT;

public class SendAction extends AnAction {

    private static final Logger LOG = LoggerFactory.getLogger(SendAction.class);

    private String data;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Object mainPanel = project.getUserData(ACTIVE_CONTENT);
        doActionPerformed((MainPanel) mainPanel, data);
    }

    private boolean presetCheck() {
        return true;
    }

    public void doActionPerformed(MainPanel mainPanel, String data) {
        // Filter the empty text
        if (StringUtils.isEmpty(data)) {
            return;
        }

        // Check the configuration first
        if (!presetCheck()) {
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
}
