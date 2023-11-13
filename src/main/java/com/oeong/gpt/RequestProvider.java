package com.oeong.gpt;

import com.intellij.openapi.project.Project;
import com.oeong.gpt.ui.MainPanel;

import java.util.Map;


public class RequestProvider {

    public static final String OFFICIAL_GPT35_TURBO_URL = "https://api.openai-proxy.com/v1/chat/completions";
    private Project myProject;
    private String url;
    private String data;
    private Map<String, String> header;

    public String getUrl() {
        return url;
    }

    public String getData() {
        return data;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public RequestProvider create(MainPanel mainPanel, String question) {
        myProject = mainPanel.getProject();
        RequestProvider provider = new RequestProvider();

        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        provider.url = OFFICIAL_GPT35_TURBO_URL;
        provider.header = TokenManager.getInstance().getGPT35TurboHeaders();
        if (instance.enableContext) {
            provider.data = OfficialBuilder.buildGpt35Turbo(question,mainPanel.getContentPanel()).toString();
        } else {
            provider.data = OfficialBuilder.buildGpt35Turbo(question).toString();
        }
        return provider;
    }
}
