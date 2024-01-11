package com.oeong.ui.dev;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.ui.components.JBScrollPane;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

import static com.oeong.ui.dev.ClipboardComponent.COPY_FLAG;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/11/22 9:36
 */
public class MyClipboard {
    public static final Key<Object> LAST_SELECTED_TEXT = Key.create("lastSelectedText");
    @Getter
    private JPanel container;
    private Project project;
    private boolean clipboardSelected = false;

    public MyClipboard(Project project) {
        this.project = project;
        initUI();
    }

    private void initUI() {
        container = new JPanel(new BorderLayout());

        ClipboardGroupComponent clipboardGroupComponent = new ClipboardGroupComponent();
        JBScrollPane jPanel = new JBScrollPane();
        jPanel.setViewportView(clipboardGroupComponent);
        jPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jPanel.updateUI();
        container.add(jPanel, BorderLayout.CENTER);
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // 获取剪贴板中的内容
        systemClipboard.addFlavorListener(new FlavorListener() {
            /**
             * Invoked when the target {@link Clipboard} of the listener has changed its
             * available {@link DataFlavor}s.
             * <p>
             * Some notifications may be redundant &#8212; they are not caused by a
             * change of the set of DataFlavors available on the clipboard. For example,
             * if the clipboard subsystem supposes that the system clipboard's contents
             * has been changed but it can't ascertain whether its DataFlavors have been
             * changed because of some exceptional condition when accessing the
             * clipboard, the notification is sent to ensure from omitting a significant
             * notification. Ordinarily, those redundant notifications should be
             * occasional.
             *
             * @param e a {@code FlavorEvent} object
             */
            @Override
            public void flavorsChanged(FlavorEvent e) {
                if (clipboardSelected != true) {
                    clipboardSelected = true;
                    return;
                }
                clipboardSelected = false;
                Clipboard clipboard = (Clipboard) e.getSource();
                Object copyFlag = project.getUserData(COPY_FLAG);
                if (copyFlag == null) {
                    project.putUserData(COPY_FLAG, false);
                    copyFlag = false;
                }
                if ((boolean) copyFlag) {
                    project.putUserData(COPY_FLAG, false);
                    return;
                }
                String text = "";
                try {
                    text = (String) clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException | IOException e1) {
                }
                String lastSelectedText = (String) project.getUserData(LAST_SELECTED_TEXT);
                if (lastSelectedText != null && !text.equals("") && lastSelectedText.equals(text)) {
                    return;
                }
//        String selectedTextHtml = selectedText.replaceAll("\n", "<br/>");
                String selectedTextHtml = "<pre>" + text + "</pre>";
                Document doc = Jsoup.parse(selectedTextHtml);
                ClipboardComponent copy = new ClipboardComponent(doc.html(), true, project);
                clipboardGroupComponent.add(copy);
                project.putUserData(LAST_SELECTED_TEXT, lastSelectedText);
            }
        });

        // 获取IDEA的剪贴板管理器
        CopyPasteManager copyPasteManager = CopyPasteManager.getInstance();
        copyPasteManager.addContentChangedListener(new CopyPasteManager.ContentChangedListener() {
            @Override
            public void contentChanged(@Nullable Transferable oldTransferable, Transferable newTransferable) {
                if (clipboardSelected != false) {
                    clipboardSelected = false;
                    return;
                }
                clipboardSelected = false;
                Object copyFlag = project.getUserData(COPY_FLAG);
                if (copyFlag == null) {
                    project.putUserData(COPY_FLAG, false);
                    copyFlag = false;
                }
                if ((boolean) copyFlag) {
                    project.putUserData(COPY_FLAG, false);
                    return;
                }
                String text = copyPasteManager.getContents(DataFlavor.stringFlavor);
                String lastSelectedText = (String) project.getUserData(LAST_SELECTED_TEXT);
                if (lastSelectedText != null && !text.equals("") && lastSelectedText.equals(text)) {
                    return;
                }
//        String selectedTextHtml = selectedText.replaceAll("\n", "<br/>");
                String selectedTextHtml = "<pre>" + text + "</pre>";
                Document doc = Jsoup.parse(selectedTextHtml);
                ClipboardComponent copy = new ClipboardComponent(doc.html(), true, project);
                clipboardGroupComponent.add(copy);
                project.putUserData(LAST_SELECTED_TEXT, text);
            }
        }, new Disposable() {
            @Override
            public void dispose() {

            }
        });
    }
}
