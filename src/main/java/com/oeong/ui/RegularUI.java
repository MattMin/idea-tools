package com.oeong.ui;

import com.intellij.openapi.project.Project;
import com.oeong.ui.dev.Regular;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RegularUI implements MenuAction {
    @Override
    public @NotNull String getName() {
        return "Regular Check";
    }

    @Override
    public @NotNull String parent() {
        return DEV;
    }

    @Override
    public int order() {
        return 4;
    }

    @Override
    public void action(ActionEvent event) {

    }

    @Override
    public @NotNull JPanel getContainer(Project project) {
        return new Regular().getContainer();
    }
}
