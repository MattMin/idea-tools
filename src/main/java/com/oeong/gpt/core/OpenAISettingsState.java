
package com.oeong.gpt.core;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@State(
        name = "com.oeong.gpt.core.OpenAISettingsState",
        storages = @Storage("ChatGPTSettingsPlugin.xml")
)
public class OpenAISettingsState implements PersistentStateComponent<OpenAISettingsState> {

    public String readTimeout = "50000";
    public String connectionTimeout = "50000";
    public String apiKey = "";
    public String gpt35Model = "gpt-3.5-turbo";
    public Boolean enableContext = false;
    public Boolean enableTokenConsumption = false;
    public Boolean enableGPT35StreamResponse = false;
    public Boolean enableLineWarp = true;
    public static final String BUG = "Find Bug";
    public static final String EXPLAIN = "Explain Exception";
    public static final String BUG_PROMPT = "Find bug from the following code: ";
    public static final String EXCEPTION_PROMPT = "explain this exception: ";

    public static OpenAISettingsState getInstance() {
        return ApplicationManager.getApplication().getService(OpenAISettingsState.class);
    }

    @Nullable
    @Override
    public OpenAISettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull OpenAISettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
