package com.oeong.gpt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;

public class OfficialParser {

    public static ParseResult parseGPT35Turbo(String response) {
        JsonObject object = JsonParser.parseString(response).getAsJsonObject();
        JsonArray choices = object.get("choices").getAsJsonArray();
        StringBuilder result = new StringBuilder();
        for (JsonElement element : choices) {
            JsonObject messages = element.getAsJsonObject().get("message").getAsJsonObject();
            String content = messages.get("content").getAsString();
            result.append(content);
        }

        OpenAISettingsState state = OpenAISettingsState.getInstance();
        StringBuilder usageResult = new StringBuilder(result);
        if (state.enableTokenConsumption) {
            JsonObject usage = object.get("usage").getAsJsonObject();
            usageResult.append("<br /><br />");
            usageResult.append("*");
            usageResult.
                    append("Prompt tokens: ").append("<b>").append(usage.get("prompt_tokens").getAsInt()).append("</b>").append(", ").
                    append("Completion tokens: ").append("<b>").append(usage.get("completion_tokens").getAsInt()).append("</b>").append(", ").
                    append("Total tokens: ").append("<b>").append(usage.get("total_tokens").getAsInt()).append("</b>");
            usageResult.append("*");
        }
        ParseResult parseResult = new ParseResult();
        parseResult.source = result.toString();
        parseResult.html = HtmlUtil.md2html(usageResult.toString());
        return parseResult;
    }

    @Getter
    public static class ParseResult {
        private String source;
        private String html;
    }

}
