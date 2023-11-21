package com.oeong.gpt;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.oeong.gpt.core.OpenAISettingsState;
import com.oeong.ui.ai.MessageGroupComponent;

/**
 * role为system可以限定AI的角色，role为user是用户发送的内容，role为assistant是AI回答的内容。
 */
public class OfficialBuilder {

    public static JsonObject buildGpt35Turbo(String text) {
        JsonObject result = new JsonObject();
        result.addProperty("model", "gpt-3.5-turbo");
        JsonArray messages = new JsonArray();
        JsonObject message0 = new JsonObject();
        message0.addProperty("role", "user");
        message0.addProperty("content", text);
        messages.add(message0);
        result.add("messages", messages);
        if (OpenAISettingsState.getInstance().enableGPT35StreamResponse) {
            result.addProperty("stream", true);
        }
        return result;
    }

    public static JsonObject buildGpt35Turbo(String text, MessageGroupComponent component) {
        JsonObject result = new JsonObject();
        OpenAISettingsState settingsState = OpenAISettingsState.getInstance();
        result.addProperty("model", settingsState.gpt35Model);
        component.getMessages().add(userMessage(text));
        result.add("messages", component.getMessages());
        if (OpenAISettingsState.getInstance().enableGPT35StreamResponse) {
            result.addProperty("stream", true);
        }
        return result;
    }

    private static JsonObject message(String role, String text) {
        JsonObject message = new JsonObject();
        message.addProperty("role", role);
        message.addProperty("content", text);
        return message;
    }

    public static JsonObject userMessage(String text) {
        return message("user", text);
    }

    public static JsonObject assistantMessage(String text) {
        return message("assistant", text);
    }
}
