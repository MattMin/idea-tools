package com.oeong.ui;

import com.intellij.openapi.project.Project;
import com.oeong.ui.dev.Clipboard;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/10/13 9:54
 */
public class ClipboardMenuItem implements MenuAction {
    /**
     * 当前菜单名称
     *
     * @return
     */
    @Override
    public @NotNull String getName() {
        return "Clipboard";
    }

    /**
     * 父级菜单名称
     *
     * @return 对应 MenuAction.FISH,MenuAction.AI,MenuAction.DEV,MenuAction.TOOLS
     */
    @Override
    public @NotNull String parent() {
        return DEV;
    }

    /**
     * 子菜单排序
     *
     * @return 数字越大越靠后
     */
    @Override
    public int order() {
        return 8;
    }

    /**
     * 菜单点击动作
     *
     * @param event 事件对象
     */
    @Override
    public void action(ActionEvent event) {

    }

    /**
     * 子菜单对应的面板
     *
     * @param project 当前工程
     * @return 面板对象
     */
    @Override
    public @NotNull JPanel getContainer(Project project) {
        Clipboard clipboard = new Clipboard(project);
        return clipboard.getContainer();
    }
}