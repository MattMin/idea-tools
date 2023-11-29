package com.oeong.action;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.oeong.notice.Notifier;
import org.jetbrains.annotations.NotNull;

import static com.oeong.action.CopyAction.LAST_SELECTED_TEXT;

public class PasteAction extends AnAction {

    public PasteAction() {
        super("Paste");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // get selected text
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert editor != null;
        Project project = e.getProject();
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile psiFile = psiDocumentManager.getPsiFile(editor.getDocument());
        if (psiFile != null) {
            int offset = editor.getCaretModel().getOffset();
            PsiElement psiElement = psiFile.findElementAt(offset);
            String lastSelectedText = (String) project.getUserData(LAST_SELECTED_TEXT);
            if (lastSelectedText == null) {
                Notifier.notifyError("Please open the Clipboard window first.");
                return;
            }
            if (lastSelectedText.equals("")) {
                Notifier.notifyError("Clipboard is empty.");
                return;
            }
            if (psiElement != null) {
                PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
                // 在这里进行对PSI的修改
                PsiFile fileFromText = psiFileFactory.createFileFromText(PlainTextFileType.INSTANCE.getLanguage(), lastSelectedText + "\n");
                CommandProcessor.getInstance().executeCommand(project, () -> {
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        // 在这里进行写操作
                        psiElement.getParent().addAfter(fileFromText, psiElement);
                    });
                }, "", null);
            }
        }
    }
}
