package com.oeong.ui;

import com.intellij.openapi.project.Project;
import com.oeong.ui.tools.SimpleCode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/9/15 9:17
 */
public class SimpleCodeMenuItem implements MenuAction {
    /**
     * 当前菜单名称
     *
     * @return
     */
    @Override
    public @NotNull String getName() {
        return "Simple code";
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
        return 0;
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
        SimpleCode simpleCode = new SimpleCode(project);
        return simpleCode.getSimpleCodeContainer(project,null);
    }



}
