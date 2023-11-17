package com.oeong.ui.ai;

import cn.hutool.core.swing.clipboard.ClipboardUtil;
import com.intellij.icons.AllIcons;
import com.intellij.notification.impl.ui.NotificationsUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.JBUI;
import com.oeong.notice.Notifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MessageComponent extends JBPanel<MessageComponent> {

    private final MessagePanel component = new MessagePanel();
    private String answer;

    public MessageComponent(String content, boolean me) {
        answer = content;
        setDoubleBuffered(true);
        setOpaque(true);
        setBackground(me ? new JBColor(0xEAEEF7, 0x45494A) : new JBColor(0xE0EEF7, 0x2d2f30 /*2d2f30*/));
        setBorder(JBUI.Borders.empty(10, 10, 10, 0));
        setLayout(new BorderLayout(JBUI.scale(7), 0));

        JPanel centerPanel = new JPanel(new VerticalLayout(JBUI.scale(8)));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(JBUI.Borders.emptyRight(10));
        centerPanel.add(createContentComponent(content));
        add(centerPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        actionPanel.setBorder(JBUI.Borders.emptyRight(10));
        JLabel copyAction = new JLabel(AllIcons.Actions.Copy);
        copyAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        copyAction.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ClipboardUtil.setStr(answer);
                Notifier.notifyInfo("Copy Success!\n" + answer);
            }
        });
        actionPanel.add(copyAction, BorderLayout.NORTH);
        add(actionPanel, BorderLayout.EAST);
    }

    public Component createContentComponent(String content) {

        component.setEditable(false);
        component.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        component.setContentType("text/html; charset=UTF-8");
        component.setOpaque(false);
        component.setBorder(null);

        NotificationsUtil.configureHtmlEditorKit(component, false);

        component.updateMessage(content);

        component.setEditable(false);
        if (component.getCaret() != null) {
            component.setCaretPosition(0);
        }

        component.revalidate();
        component.repaint();

        return component;
    }

    public void setSourceContent(String source) {
        answer = source;
    }

    public void setContent(String content) {
        new MessageWorker(content).execute();
    }

    class MessageWorker extends SwingWorker<Void, String> {
        private final String message;

        public MessageWorker(String message) {
            this.message = message;
        }

        @Override
        protected Void doInBackground() {
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                component.updateMessage(message);
                component.updateUI();
            } catch (Exception e) {

                Notifier.notifyError("ChatGPT Exception in processing response: response:" + message
                        + " error: " + e.getMessage());
            }
        }
    }

    public void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            Rectangle bounds = getBounds();
            scrollRectToVisible(bounds);
        });
    }
}
