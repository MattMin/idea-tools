package com.oeong.ui;


import com.intellij.openapi.project.Project;
import com.oeong.ui.dev.UUID;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class UUIDMenuItem implements MenuAction {

    //当前菜单名称
    @Override
    public @NotNull String getName() {
        return "UUID";
    }
    // 一级菜单名称
    @Override
    public @NotNull String parent() {
        return DEV;
    }

    //子菜单排序
    @Override
    public int order() {
        return 1;
    }

    @Override
    public void action(ActionEvent event) {
    }

    // 菜单对应的面板
    @Override
    public @NotNull JPanel getContainer(Project project) {
        return new UUID().getContainer();
    }
}
