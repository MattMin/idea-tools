package com.oeong.ui;


import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

public interface MenuAction {

    String TOOLS = "Tools";
    String FISH = "Fun";
    String AI = "AI";
    String DEV = "Dev";

    /**
     * 当前菜单名称
     * @return
     */
    @NotNull  String getName();

    /**
     * 父级菜单名称
     * @return 对应 MenuAction.FISH,MenuAction.AI,MenuAction.DEV,MenuAction.TOOLS
     */
    @NotNull
    String parent();

    /**
     * 子菜单排序
     * @return 数字越大越靠后
     */
    int order();

    /**
     * 菜单点击动作
     * @param event 事件对象
     */
    void action(ActionEvent event);


    /**
     * 子菜单对应的面板
     * @param project 当前工程
     * @return 面板对象
     */
    @NotNull
    JPanel getContainer(Project project);

}
