package com.oeong.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.oeong.vo.ApiInfo;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(name = "Mulan", storages = {
        @Storage(StoragePathMacros.WORKSPACE_FILE)
})
public class ApiInfosService implements PersistentStateComponent<ApiInfosService.State> {

    public static ApiInfosService getInstance(Project project) {
        return project.getService(ApiInfosService.class);
    }

    @Data
    static class State {
        private List<ApiInfo> apiInfos = new ArrayList<>();
    }

    private State myState = new State();

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public List<ApiInfo> getApiInfos() {
        return myState.getApiInfos();
    }

}
