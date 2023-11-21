package com.oeong.gpt;

import com.oeong.gpt.core.OpenAISettingsState;
import com.oeong.ui.ai.MainPanel;
import lombok.Getter;

import java.util.Map;


@Getter
public class RequestProvider {

    private String url;
    private String data;
    private Map<String, String> header;

    public RequestProvider create(MainPanel mainPanel, String question) {
        RequestProvider provider = new RequestProvider();

        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        provider.url = instance.OPENAI_URL;
        provider.header = TokenManager.getInstance().getGPT35TurboHeaders();
        if (instance.enableContext) {
            provider.data = OfficialBuilder.buildGpt35Turbo(question, mainPanel.getContentPanel()).toString();
        } else {
            provider.data = OfficialBuilder.buildGpt35Turbo(question).toString();
        }
        return provider;
    }
}
