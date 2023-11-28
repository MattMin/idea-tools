package com.oeong.action;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.oeong.notice.Notifier;
import com.oeong.ui.dev.ClipboardComponent;
import com.oeong.ui.dev.ClipboardGroupComponent;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static com.oeong.ui.dev.Clipboard.CLIPBOARD;

public class CopyAction extends AnAction {

    public static final Key<Object> LAST_SELECTED_TEXT = Key.create("lastSelectedText");

    public CopyAction() {
        super("Copy");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // get selected text
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert editor != null;
        String selectedText = editor.getSelectionModel().getSelectedText();

        if (selectedText == null) {
            Notifier.notifyError("No text selected");
            return;
        }

        // get main panel
        Project project = e.getProject();
        assert project != null;
        ClipboardGroupComponent mainPanel = (ClipboardGroupComponent) project.getUserData(CLIPBOARD);
        if (mainPanel == null) {
            Notifier.notifyError("Please open the Clipboard window first.");
            return;
        }
        String lastSelectedText = (String) project.getUserData(LAST_SELECTED_TEXT);
        if (lastSelectedText != null && lastSelectedText.equals(selectedText)) {
            return;
        }
//        String selectedTextHtml = selectedText.replaceAll("\n", "<br/>");
        String selectedTextHtml = "<pre>" + selectedText + "</pre>";
        Document doc = Jsoup.parse(selectedTextHtml);
        ClipboardComponent copy = new ClipboardComponent(doc.html(), true);
        mainPanel.add(copy);
        project.putUserData(LAST_SELECTED_TEXT, selectedText);
    }
}
