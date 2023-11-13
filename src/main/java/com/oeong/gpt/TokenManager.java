package com.oeong.gpt;

import com.intellij.openapi.application.ApplicationManager;

import java.util.HashMap;
import java.util.Map;


public class TokenManager {
    private final Map<String, String> headers = new HashMap<>();
    private final OpenAISettingsState settings = OpenAISettingsState.getInstance();

    public static TokenManager getInstance() {
        return ApplicationManager.getApplication().getService(TokenManager.class);
    }

    public Map<String, String> getGPT35TurboHeaders() {
        headers.put("Authorization", "Bearer " + settings.apiKey);
        headers.put("Content-Type", "application/json");
        return headers;
    }
}
