package com.oeong.ui.dev;

import com.intellij.openapi.project.Project;
import com.oeong.notice.Notifier;
import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/11/29 10:41
 */
public class ConversionSql {
    private JButton conversionButton;
    @Getter
    private JPanel container;
    private JTextArea inputArea;
    private JTextArea resultArea;
    private Project project;

    public ConversionSql(Project project) {


        inputArea.setToolTipText("输入mybatis日志");
        resultArea.setToolTipText("SQL转换结果");
        conversionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputStr = inputArea.getText();
                if (inputStr == null || inputStr.equals("")) {
                    Notifier.notifyError("请输入需要转换的内容");
                } else {
                    if (inputStr.contains("Preparing: ") && inputStr.contains("Parameters: ")) {
                        String result = textToSql(inputStr);
                        resultArea.setText(result);
                    } else {
                        Notifier.notifyError("输入的内容非法");
                    }
                }
            }
        });
    }

//    public void initUI() {
//        container = new JPanel(new FlowLayout(FlowLayout.LEFT)))
//        this.project = project;
//    }

    public String textToSql(String inputStr) {
        // 获取带问号的SQL语句
        int statementStartIndex = inputStr.indexOf("Preparing: ");
        int statementEndIndex = inputStr.length() - 1;
        for (int i = statementStartIndex; i < inputStr.length(); i++) {
            if ((inputStr.charAt(i) + "").equals("\n")) {
                statementEndIndex = i;
                break;
            }
        }
        String statementStr = inputStr.substring(statementStartIndex + "Preparing: ".length(), statementEndIndex);
        //获取参数
        int parametersStartIndex = inputStr.indexOf("Parameters: ");
        int parametersEndIndex = inputStr.length() - 1;
        for (int i = parametersStartIndex; i < inputStr.length(); i++) {
            if ((inputStr.charAt(i) + "").equals("\n")) {
                parametersEndIndex = i;
                break;
            }
        }
        String parametersStr = inputStr.substring(parametersStartIndex + "Parameters: ".length(), parametersEndIndex + 1);
        String[] splitParameters = parametersStr.split(",");

        for (var i = 0; i < splitParameters.length; i++) {
            // 如果数据中带括号将使用其他逻辑
            String tempStr = splitParameters[i].substring(0, splitParameters[i].indexOf("("));
            // 获取括号中内容
            String typeStr = splitParameters[i].substring(splitParameters[i].indexOf("(") + 1, splitParameters[i].indexOf(")"));
            // 如果为字符类型
            if (typeStr == "String" || typeStr == "Timestamp") {
                statementStr = statementStr.replace("?", '"' + tempStr.trim() + '"');
            } else {
                // 数值类型
                statementStr = statementStr.replace("?", tempStr.trim());
            }
        }
        return statementStr;
    }
}
