package com.oeong.ui.fish;

import com.oeong.tools.TrigramTools;
import com.oeong.tools.TrigramVo;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Divination {
    private JPanel container;
    private JComboBox comboBox1;
    private JButton divineButton;
    private JTextPane trigramImg;
    private JTextPane detail;

    public Divination(){
        divineButton.addActionListener(this::divine);
    }

    private final static TrigramTools tools = new TrigramTools();

    public void divine(ActionEvent e){
        String item = (String) comboBox1.getSelectedItem();
        TrigramVo vo = tools.divine(item);
        if(vo!=null){
            trigramImg.setText(vo.getName()+":"+vo.getImg());
            detail.setText("卦辞："+vo.getOriginal()+"\r"+item+"："+vo.getProphecy());
        }
    }




    public JPanel getContainer() {
        return container;
    }
}
