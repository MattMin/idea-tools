package com.oeong.ui.tools;

import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.Project;
import com.oeong.tools.ApiSettingManager;
import com.oeong.vo.ApiInfo;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/10/11 10:41
 */
public class Ocr {
    private Project project;

    private JPanel container;
    private JPanel ocrPanel;
    private JScrollPane connectionListPanel;
    private JList connectionJList;
    private Map<String, ApiInfo> connectionMap;
    private ApiInfo apiInfo;
    private ApiSettingManager apiSettingManager;


    public Ocr(Project project) {
        this.project = project;
        initUI();
    }

    private void initUI() {
        container = new JPanel(new BorderLayout());
        ocrPanel = new JPanel();
        ocrPanel.setLayout(new BorderLayout());
        //添加工具栏
        apiSettingManager = ApiSettingManager.getInstance(project);
//        ApiInfo conConnectionDefault = apiSettingManager.getConConnectionDefault();
        ActionToolbar ocrToolbar = apiSettingManager.createOcrToolbar(null, container);
        container.add(ocrToolbar.getComponent(), BorderLayout.NORTH);
        //初始化连接
        apiSettingManager.createListPanel();
        container.add(ocrPanel, BorderLayout.SOUTH);
    }


    public JPanel getContainer() {
        return container;
    }

    public void setContainer(JPanel container) {
        this.container = container;
    }

    public JPanel getOcrPanel() {
        return ocrPanel;
    }

    public void setOcrPanel(JPanel ocrPanel) {
        this.ocrPanel = ocrPanel;
    }

    public ApiInfo getConnectionInfo() {
        return apiInfo;
    }

    public void setConnectionInfo(ApiInfo apiInfo) {
        this.apiInfo = apiInfo;
    }


}
