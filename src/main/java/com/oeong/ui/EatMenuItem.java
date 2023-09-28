package com.oeong.ui;

import com.intellij.openapi.project.Project;
import com.oeong.ui.fish.WhatToEat;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EatMenuItem implements MenuAction {

    // 当前菜单名称
    @Override
    public @NotNull String getName() {
        return "what to eat";
    }

    // 一级菜单名称
    @Override
    public @NotNull String parent() {
        return FISH;
    }

    // 子菜单排序
    @Override
    public int order() {
        return 1;
    }

    @Override
    public void action(ActionEvent event) {
        System.out.println(event);
    }

    // 菜单对应的面板
    @Override
    public @NotNull JPanel getContainer(Project project) {
        return new WhatToEat().getComponent();
    }
}
