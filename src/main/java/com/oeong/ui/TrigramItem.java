package com.oeong.ui;

import com.intellij.openapi.project.Project;
import com.oeong.ui.fish.Divination;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TrigramItem implements MenuAction{
    @Override
    public @NotNull String getName() {
        return "Trigram";
    }

    @Override
    public @NotNull String parent() {
        return FISH;
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void action(ActionEvent event) {

    }

    @Override
    public @NotNull JPanel getContainer(Project project) {
        return new Divination().getContainer();
    }
}
