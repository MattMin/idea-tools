package com.oeong.gpt.ui;

import com.intellij.find.SearchTextArea;
import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.laf.darcula.ui.DarculaButtonUI;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.JBTextArea;
import com.oeong.gpt.OpenAISettingsState;
import com.oeong.gpt.core.SendListener;
import lombok.Getter;
import okhttp3.Call;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainPanel {

    @Getter
    private final SearchTextArea searchTextArea;
    @Getter
    private final JButton button;
    private final JButton stopGenerating;
    @Getter
    private final MessageGroupComponent contentPanel;
    private final JProgressBar progressBar;
    private final OnePixelSplitter splitter;
    private final Project myProject;
    private final JPanel actionPanel;
    private ExecutorService executorService;
    private Object requestHolder;

    public MainPanel(@NotNull Project project) {
        myProject = project;
        SendListener listener = new SendListener(this);

        splitter = new OnePixelSplitter(true, .98f);
        splitter.setDividerWidth(2);

        // Search text area
        searchTextArea = new SearchTextArea(new JBTextArea(), true);
        searchTextArea.getTextArea().addKeyListener(listener);
        searchTextArea.setMinimumSize(new Dimension(searchTextArea.getWidth(), 500));
        searchTextArea.setMultilineEnabled(OpenAISettingsState.getInstance().enableLineWarp);

        // Send button
        button = new JButton("send", IconLoader.getIcon("/icons/send.svg", MainPanel.class));
        button.addActionListener(listener);
        button.setUI(new DarculaButtonUI());

        // Stop button
        stopGenerating = new JButton("Stop", AllIcons.Actions.Suspend);
        stopGenerating.addActionListener(e -> {
            executorService.shutdownNow();
            aroundRequest(false);
            if (requestHolder instanceof EventSource) {
                ((EventSource) requestHolder).cancel();
            } else if (requestHolder instanceof Call) {
                ((Call) requestHolder).cancel();
            }
        });
        stopGenerating.setUI(new DarculaButtonUI());

        actionPanel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        actionPanel.add(progressBar, BorderLayout.NORTH);
        actionPanel.add(searchTextArea, BorderLayout.CENTER);
        actionPanel.add(button, BorderLayout.EAST);
        contentPanel = new MessageGroupComponent();

        splitter.setFirstComponent(contentPanel);
        splitter.setSecondComponent(actionPanel);
    }

    public Project getProject() {
        return myProject;
    }

    public JPanel init() {
        return splitter;
    }

    public ExecutorService getExecutorService() {
        executorService = Executors.newFixedThreadPool(1);
        return executorService;
    }

    public void aroundRequest(boolean status) {
        progressBar.setIndeterminate(status);
        progressBar.setVisible(status);
        button.setEnabled(!status);
        if (status) {
            contentPanel.addScrollListener();
            actionPanel.remove(button);
            actionPanel.add(stopGenerating, BorderLayout.EAST);
        } else {
            contentPanel.removeScrollListener();
            actionPanel.remove(stopGenerating);
            actionPanel.add(button, BorderLayout.EAST);
        }
        actionPanel.updateUI();
    }

    public void setRequestHolder(Object eventSource) {
        this.requestHolder = eventSource;
    }
}
