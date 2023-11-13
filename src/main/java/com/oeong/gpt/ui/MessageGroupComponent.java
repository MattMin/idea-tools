package com.oeong.gpt.ui;

import com.google.gson.JsonArray;
import com.intellij.openapi.ui.NullableComponent;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.oeong.gpt.GPTSettingDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MessageGroupComponent extends JBPanel<MessageGroupComponent> implements NullableComponent {

    private final JPanel myList = new JPanel(new VerticalLayout(JBUI.scale(10)));
    private final MyScrollPane myScrollPane = new MyScrollPane(myList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    private int myScrollValue = 0;
    private final MessageComponent tips =
            new MessageComponent("Use 'Find Bug' in the Editor or 'Explain Exception' in the Console.", false);

    private final MyAdjustmentListener scrollListener = new MyAdjustmentListener();
    private JsonArray messages = new JsonArray();


    public MessageGroupComponent() {
        setBorder(JBUI.Borders.empty(10, 10, 10, 0));
        setLayout(new BorderLayout(JBUI.scale(7), 0));
        setBackground(UIUtil.getListBackground());

        JPanel mainPanel = new JPanel(new BorderLayout(0, JBUI.scale(8)));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(JBUI.Borders.emptyLeft(8));

        add(mainPanel, BorderLayout.CENTER);

        // myTitle
        JBLabel myTitle = new JBLabel("Conversation");
        myTitle.setForeground(JBColor.namedColor("Label.infoForeground", new JBColor(Gray.x80, Gray.x8C)));
        myTitle.setFont(JBFont.label());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(JBUI.Borders.empty(0, 10, 10, 0));

        panel.add(myTitle, BorderLayout.WEST);

        // clear
        LinkLabel<String> newChat = new LinkLabel<>("Clear", null);
        newChat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                myList.removeAll();
                myList.add(tips);
                myList.updateUI();
                messages = new JsonArray();
            }
        });
        newChat.setFont(JBFont.label());
        newChat.setBorder(JBUI.Borders.emptyRight(20));

        // config
        LinkLabel<String> config = new LinkLabel<>("Configuration", null);
        config.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                GPTSettingDialog apiKeySettingsDialog = new GPTSettingDialog();
                apiKeySettingsDialog.show();
            }
        });
        config.setFont(JBFont.label());
        config.setBorder(JBUI.Borders.emptyRight(20));

        // 将 config 和 newChat 放在同一个 FlowLayout 中
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(newChat);
        buttonsPanel.add(config);
        panel.add(buttonsPanel, BorderLayout.EAST);

        mainPanel.add(panel, BorderLayout.NORTH);

        // myScrollPane
        myList.setOpaque(true);
        myList.setBackground(UIUtil.getListBackground());
        myList.setBorder(JBUI.Borders.emptyRight(10));

        myScrollPane.setBorder(JBUI.Borders.empty());
        mainPanel.add(myScrollPane);
        myScrollPane.getVerticalScrollBar().setAutoscrolls(true);
        myScrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            int value = e.getValue();
            if (myScrollValue == 0 && value > 0 || myScrollValue > 0 && value == 0) {
                myScrollValue = value;
                repaint();
            } else {
                myScrollValue = value;
            }
        });

        // Add the default message
        add(tips);
    }

    public void add(MessageComponent messageComponent) {
        myList.add(messageComponent);
        updateLayout();
        scrollToBottom();
        updateUI();
    }

    public void scrollToBottom() {
        JScrollBar verticalScrollBar = myScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
    }

    public void updateLayout() {
        LayoutManager layout = myList.getLayout();
        int componentCount = myList.getComponentCount();
        for (int i = 0; i < componentCount; i++) {
            layout.removeLayoutComponent(myList.getComponent(i));
            layout.addLayoutComponent(null, myList.getComponent(i));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (myScrollValue > 0) {
            g.setColor(JBColor.border());
            int y = myScrollPane.getY() - 1;
            g.drawLine(0, y, getWidth(), y);
        }
    }

    @Override
    public boolean isVisible() {
        if (super.isVisible()) {
            int count = myList.getComponentCount();
            for (int i = 0; i < count; i++) {
                if (myList.getComponent(i).isVisible()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isNull() {
        return !isVisible();
    }

    public void addScrollListener() {
        myScrollPane.getVerticalScrollBar().
                addAdjustmentListener(scrollListener);
    }

    public void removeScrollListener() {
        myScrollPane.getVerticalScrollBar().
                removeAdjustmentListener(scrollListener);
    }

    static class MyAdjustmentListener implements AdjustmentListener {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            JScrollBar source = (JScrollBar) e.getSource();
            if (!source.getValueIsAdjusting()) {
                source.setValue(source.getMaximum());
            }
        }
    }

    public JsonArray getMessages() {
        return messages;
    }
}
