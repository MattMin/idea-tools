package com.oeong.ui.dev;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.intellij.openapi.project.Project;
import com.oeong.notice.Notifier;
import groovy.json.StringEscapeUtils;
import lombok.Getter;
import org.jdesktop.swingx.JXRadioGroup;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @descriptions: YAML/JSON/XML/HTML 格式化
 * @author: Zzw
 * @date: 2023/11/6 9:37
 */
public class FileFormat {
    static Map<String, Function<String, String>> encryptMap = new HashMap<>();

    static {
        encryptMap.put("YAML", FileFormat::yamlTool);
        encryptMap.put("JSON", FileFormat::jsonTool);
        encryptMap.put("XML", FileFormat::xmlTool);
        encryptMap.put("HTML", FileFormat::htmlTool);
    }

    private Project project;
    private JTextArea inputArea;
    private JTextArea resultArea;
    private JRadioButton yamlRadioButton;
    private JRadioButton jsonRadioButton;
    private JRadioButton xmlRadioButton;
    private JRadioButton htmlRadioButton;
    private JButton formatButton;
    private JPanel buttonPanel;
    @Getter
    private JPanel container;
    private JXRadioGroup<JRadioButton> group;

    private String radioSelected = "";

    public FileFormat(Project project) {
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

    public static String yamlTool(String text) {
        Yaml yamText = new Yaml();
        try {
            yamText.load(new StringReader(text));
        } catch (YAMLException e) {
            Notifier.notifyError("Incorrect format of YAML");
        }
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);
        StringWriter writer = new StringWriter();
        yaml.dump(text, writer);
        return writer.toString();
    }

    public static String jsonTool(String text) {
        String res = StringEscapeUtils.unescapeJava(text);
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONUtil.parseObj(res);
        } catch (Exception e) {
            Notifier.notifyError("Incorrect format of JSON");
            return null;
        }
        return jsonObject.toStringPretty();
    }

    public static String xmlTool(String text) {
        StringWriter writer = null;
        try {
            SAXBuilder builder = new SAXBuilder();
            StringReader reader = new StringReader(text);
            org.jdom.Document build = builder.build(reader);
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            writer = new StringWriter();
            xmlOutputter.output(build, writer);
        } catch (Exception e) {
            Notifier.notifyError("Incorrect format of XML");
            return null;
        }
        return writer.toString();
    }

    public static String htmlTool(String text) {
        if (!text.startsWith("<html")) {
            Notifier.notifyError("Incorrect format of HTML");
            return null;
        }
        Document document = Jsoup.parse(text);
        return document.html();
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
                Function<String, String> function = encryptMap.get(text);
                String input = inputArea.getText();
                if (input == null || "".equals(input)) {
                    Notifier.notifyError("请输入需要格式化的内容");
                    return;
                }
                String result = function.apply(input);
                resultArea.setText(result);
                resultArea.updateUI();
            }
        });

        radioSelected = text;
    }


}
