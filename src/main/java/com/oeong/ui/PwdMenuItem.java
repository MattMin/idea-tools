package com.oeong.ui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class PwdMenuItem implements MenuAction {
    @Override
    public @NotNull String getName() {
        return "password";
    }

    @Override
    public @NotNull String parent() {
        return TOOLS;
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void action(ActionEvent event) {
        System.out.println(event);
    }

    @Override
    public @NotNull JPanel getContainer(Project project) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new RandomPwdUI().getContainer());
        return new RandomPwdUI().getContainer();
    }
}
