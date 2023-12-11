package com.oeong.ui.dev;

import com.intellij.json.JsonLanguage;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorSettingsProvider;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.LanguageTextField;
import com.oeong.notice.Notifier;
import lombok.Getter;
import org.jdesktop.swingx.JXRadioGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLLanguage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @descriptions: YAML/JSON/XML/HTML 格式化
 * @author: Zzw
 * @date: 2023/11/6 9:37
 */
public class FileFormat {
    static Map<String, Consumer<String>> encryptMap = new HashMap<>();
    private static Project project;
    private static EditorTextField resultArea;

    static {
        encryptMap.put("YAML", FileFormat::yamlTool);
        encryptMap.put("JSON", FileFormat::jsonTool);
        encryptMap.put("XML", FileFormat::xmlTool);
        encryptMap.put("HTML", FileFormat::htmlTool);
    }

    private JTextArea inputArea;
    private JRadioButton yamlRadioButton;
    private JRadioButton jsonRadioButton;
    private JRadioButton xmlRadioButton;
    private JRadioButton htmlRadioButton;
    private JButton formatButton;
    private JPanel buttonPanel;
    @Getter
    private JPanel container;
    private JXRadioGroup<JRadioButton> group;
    private JScrollPane inputPanel;
    private JScrollPane resultPanel;

    private String radioSelected = "";

    public FileFormat(Project project) {
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputPanel.setViewportView(inputArea);
        // 禁用水平滚动条
        inputPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        resultArea = new LanguageTextField(PlainTextLanguage.INSTANCE, project, "", false);
        resultPanel.setViewportView(resultArea);
        yamlRadioButton = new JRadioButton("YAML");
        jsonRadioButton = new JRadioButton("JSON");
        xmlRadioButton = new JRadioButton("XML");
        htmlRadioButton = new JRadioButton("HTML");
        yamlRadioButton.setSelected(true);
        group.add(yamlRadioButton);
        // 添加水平间距
        group.add(Box.createHorizontalStrut(10));
        group.add(jsonRadioButton);
        // 添加水平间距
        group.add(Box.createHorizontalStrut(10));
        group.add(xmlRadioButton);
        // 添加水平间距
        group.add(Box.createHorizontalStrut(10));
        group.add(htmlRadioButton);

        yamlRadioButton.setSelected(true);
        changeRadio("YAML");
        // 添加监听器到按钮组上
        group.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButton selectedValue = group.getSelectedValue();
                String text = selectedValue.getText();
                if (!text.equals(radioSelected)) {
                    changeRadio(text);
                }
            }
        });
        this.project = project;
    }

    public static void yamlTool(String text) {
        resultArea = new LanguageTextField(YAMLLanguage.INSTANCE, project, text, false);
    }

    public static void jsonTool(String text) {
        resultArea = new LanguageTextField(JsonLanguage.INSTANCE, project, text, false);

    }

    public static void xmlTool(String text) {
        resultArea = new LanguageTextField(XMLLanguage.INSTANCE, project, text, false);
    }

    public static void htmlTool(String text) {
        resultArea = new LanguageTextField(HTMLLanguage.INSTANCE, project, text, false);
    }

    private void changeRadio(String text) {
        ActionListener[] actionListeners = formatButton.getActionListeners();
        if (actionListeners != null) {
            for (ActionListener actionListener : actionListeners) {
                formatButton.removeActionListener(actionListener);
            }
        }

        formatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Consumer<String> function = encryptMap.get(text);
                String input = inputArea.getText();
                if (input == null || "".equals(input)) {
                    //
                    Notifier.notifyError("请输入需要格式化的内容");
                    return;
                }
                function.accept(input);
                resultArea.setAutoscrolls(true);
                resultArea.setOneLineMode(false);
                resultArea.setMinimumSize(new Dimension(100, 100));
                resultArea.addSettingsProvider(new EditorSettingsProvider() {
                    @Override
                    public void customizeSettings(EditorEx editorEx) {
                        EditorSettings settings = editorEx.getSettings();
                        settings.setUseSoftWraps(true);
                        settings.setWhitespacesShown(true);
                        settings.setLeadingWhitespaceShown(true);
                        // 启用自动换行
                        settings.setUseSoftWraps(true);
                    }
                });
                ActionManager am = ActionManager.getInstance();
                am.getAction("ReformatCode").actionPerformed(
                        new AnActionEvent(
                                null,
                                new DataContext() {
                                    /**
                                     * Returns the object corresponding to the specified data identifier. Some of the supported
                                     * data identifiers are defined in the {@link PlatformDataKeys} class.
                                     *
                                     * <b>NOTE:</b> For implementation only, prefer {@link DataContext#getData(DataKey)} in client code.
                                     *
                                     * @param dataId the data identifier for which the value is requested.
                                     * @return the value, or null if no value is available in the current context for this identifier.
                                     */
                                    @Override
                                    public @Nullable Object getData(@NotNull String dataId) {
                                        // 在这里根据需要提供数据
                                        if (CommonDataKeys.PROJECT.is(dataId)) {
                                            // 在这个例子中，我们提供一个虚拟的项目对象
                                            return project;
                                        }
                                        if (CommonDataKeys.EDITOR.is(dataId)) {
                                            Document document = resultArea.getDocument();
                                            Editor editor = EditorFactory.getInstance().createEditor(document, project);
                                            return editor;
                                        }
                                        return null;
                                    }
                                },
//                                DataManager.getInstance().getDataContext(resultArea),
                                ActionPlaces.UNKNOWN,
                                new Presentation(),
                                ActionManager.getInstance(),
                                0
                        )
                );
                resultPanel.setViewportView(resultArea);
                resultPanel.updateUI();
            }
        });

        radioSelected = text;
    }
}


