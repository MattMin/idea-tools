package com.oeong.ui;

import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/9/18 16:43
 */
public class MyTabbedPaneUI extends BasicTabbedPaneUI {
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        //自定义选项卡的高
        return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight);
    }

    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        //自定义选项卡的宽
        return 100;
    }

    /**
     * 自定义选项卡的背景色
     *
     * @param g 图形设置
     * @param tabPlacement 标签位置
     * @param tabIndex 标签下标
     * @param x x轴
     * @param y y轴
     * @param w 宽
     * @param h 高
     * @param isSelected 是否被选中
     */
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
        Color defaultColor = new Color(164, 135, 217);
        Color selectedColor = new Color(132, 99, 201);
        //设置选中时和未被选中时的颜色
        g.setColor(!isSelected ? defaultColor : selectedColor);
        //填充图形，即选项卡为矩形
        g.fillRect(x , y, w, h);
    }
}
