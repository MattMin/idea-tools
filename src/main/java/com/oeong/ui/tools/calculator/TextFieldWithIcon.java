package com.oeong.ui.tools.calculator;

import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class TextFieldWithIcon extends JTextField {
    private final Icon myIcon;

    public TextFieldWithIcon(@NotNull Icon icon) {
        super("");
        myIcon = icon;
        this.setMargin(JBUI.insets(0, 40));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int iconWidth = myIcon.getIconWidth();
        int iconHeight = myIcon.getIconHeight();
        int height = this.getHeight();
        myIcon.paintIcon(this, g, (30 - iconWidth), (height - iconHeight) / 2);
    }
}
