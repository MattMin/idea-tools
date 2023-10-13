package com.oeong.ui.fish;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.oeong.tools.TrigramTools;
import com.oeong.tools.TrigramVo;
import com.oeong.ui.TrigramItem;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.Locale;

public class Divination {
    private JButton divineButton;
    private JComboBox comboBox1;
    private JTextPane detail;
    private JTextArea trigramImg;
    private JPanel container;


    public Divination(){
        divineButton.addActionListener(e->divine());
    }

    TrigramTools tools = new TrigramTools();

    public void divine(){
        String item = (String) comboBox1.getSelectedItem();
        TrigramVo vo = tools.divine(item);
        trigramImg.setText(vo.getName()+":"+vo.getImg());
        detail.setText("卦辞："+vo.getOriginal()+"\t"+item+"："+vo.getProphecy());
    }




    public JPanel getContainer() {
        return container;
    }

    public JButton getDivineButton() {
        return divineButton;
    }

    public JComboBox getComboBox1() {
        return comboBox1;
    }

    public JTextPane getDetail() {
        return detail;
    }

    public JTextArea getTrigramImg() {
        return trigramImg;
    }
}
