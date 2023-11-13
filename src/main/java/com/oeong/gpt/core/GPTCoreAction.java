package com.oeong.gpt.core;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.oeong.gpt.ui.MainPanel;
import com.oeong.gpt.ui.MessageComponent;
import com.oeong.gpt.ui.MessageGroupComponent;
import com.oeong.notice.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static com.oeong.gpt.GPT.ACTIVE_COMPONENT;
import static com.oeong.gpt.GPT.ACTIVE_CONTENT;
import static com.oeong.gpt.GPT.ACTIVE_GPTPANEL;

public class GPTCoreAction {

    private static final Logger LOG = LoggerFactory.getLogger(GPTCoreAction.class);

    public static final String BUG = "Find Bug";
    public static final String EXPLAIN = "Explain Exception";
    public static final String BUG_PROMPT = "find bugs in this code: ";
    public static final String EXCEPTION_PROMPT = "explain this exception: ";
    public static final String OPENAI_URL = "https://api.openai-proxy.com/v1/chat/completions";
    PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

    public void doActionPerformed(AnActionEvent e, String prompt) {
        Project project = e.getProject();
        JPanel container = (JPanel)project.getUserData(ACTIVE_CONTENT);
        JPanel gptPanel = (JPanel)project.getUserData(ACTIVE_GPTPANEL);
//        MessageGroupComponent component = (MessageGroupComponent)project.getUserData(ACTIVE_COMPONENT);
        MainPanel mainPanel = (MainPanel)project.getUserData(ACTIVE_COMPONENT);

        if (mainPanel == null || gptPanel == null || container == null) {
            Notifier.notifyWarn("Please open GPT page first.");
            return;
        }

        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert editor != null;
        String selectedText = editor.getSelectionModel().getSelectedText();
        String question = prompt + selectedText;

        chatGPT(question, container, gptPanel, mainPanel);
    }


    public void chatGPT(String question, JPanel container, JPanel gptPanel, MainPanel component) {
        addComponent(true, container, gptPanel, component, question);

        String openaiKey = propertiesComponent.getValue("openaiKey");
        String openaiModel = propertiesComponent.getValue("openaiModel");
        String apiKey = StringUtil.isNotEmpty(openaiKey) ? openaiKey : "";
        String model = StringUtil.isNotEmpty(openaiModel) ? openaiModel : "";

        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", question);
        JsonArray messagesArray = new JsonArray();
        messagesArray.add(message);
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.add("messages", messagesArray);

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        new Thread(() -> {
            try {
                HttpResponse<String> respond = client.send(request, HttpResponse.BodyHandlers.ofString());
                String respondBody = respond.body();
                LOG.info("respondBody: {}", respondBody);
                System.out.println("respondBody: " + respondBody);

                JSONObject bodyJson = JSONUtil.parseObj(respondBody);
                JSONArray choices = bodyJson.getJSONArray("choices");
                if (choices != null) {
                    String answer = choices.getJSONObject(0).getJSONObject("message").getStr("content");
                    addComponent(false, container, gptPanel, component, answer);
                } else {
                    String error = bodyJson.getJSONObject("error").getStr("message");
                    Notifier.notifyError(error);
                    addComponent(false, container, gptPanel, component, error);
                }
            } catch (Exception e) {
                String notice = "There is something wrong in ChatGPT, please try again later.";
                Notifier.notifyError(notice);
//                addComponent(false, container, gptPanel, component, notice);
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void addComponent(boolean me, JPanel container, JPanel gptPanel, MainPanel mainPanel, String question) {
        MessageGroupComponent component = mainPanel.getContentPanel();
        MessageComponent messageComponent = new MessageComponent(question, me);
        component.add(messageComponent);
        gptPanel.add(component, BorderLayout.CENTER);
        container.add(gptPanel, BorderLayout.CENTER);
    }
}
