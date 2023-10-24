package com.oeong.ui.tools;

import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.Project;
import com.oeong.vo.ConnectionInfo;
import com.oeong.tools.ConnectionManager;

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
    private Map<String, ConnectionInfo> connectionMap;
    private ConnectionInfo connectionInfo;
    private ConnectionManager connectionManager;


    public Ocr(Project project) {
        this.project = project;
        initUI();
    }

    private void initUI() {
        container = new JPanel(new BorderLayout());
        ocrPanel = new JPanel();
        ocrPanel.setLayout(new BorderLayout());

        //添加工具栏
        connectionManager = ConnectionManager.getInstance(project);
        ActionToolbar ocrToolbar = connectionManager.createOcrToolbar(connectionInfo,container);
        container.add(ocrToolbar.getComponent(), BorderLayout.NORTH);
        //初始化连接
        connectionManager.createListPanel();
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

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }


}
