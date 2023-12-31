package com.oeong.tools;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.oeong.action.AddAction;
import com.oeong.action.DeleteAction;
import com.oeong.action.OcrAction;
import com.oeong.action.ScreenshotAction;
import com.oeong.dialog.ApiKeySettingsDialog;
import com.oeong.dialog.ConfirmDialog;
import com.oeong.dialog.OcrDialog;
import com.oeong.vo.ApiInfo;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oeong.tools.ScreenshotTools.startScreenshot;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/10/18 10:59
 */
public class ApiSettingManager {
    private Project project;
    private JScrollPane connectionListPanel;
    private JList connectionJList;
    private JPanel container;
    private PropertyUtil propertyUtil;
    private ApiInfo apiInfo;
    private boolean ocrRegisterFlag = true;
    private boolean screenshotRegisterFlag = true;

    /**
     * 插件初始化时会调用
     *
     * @param project
     */
    protected ApiSettingManager(Project project) {
        this.project = project;
        this.propertyUtil = PropertyUtil.getInstance(project);
    }

    public static ApiSettingManager getInstance(Project project) {
        return project.getService(ApiSettingManager.class);
    }


    public ActionToolbar createOcrToolbar(ApiInfo apiInfo, JPanel container) {
        if (apiInfo != null) {
            this.apiInfo = apiInfo;
        }
        this.container = container;
        // 工具栏
        DefaultActionGroup actions = new DefaultActionGroup();
        // 增加key
        actions.add(createAddAction(apiInfo));
        // 删除key
        actions.add(createDeleteAction(apiInfo));
        actions.addSeparator();
        // 截图
        actions.add(createScreenshotAction());
        // ocr
        actions.add(createOcrAction());

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("ToolwindowToolbar", actions, true);
        actionToolbar.setTargetComponent(container);
        actionToolbar.adjustTheSameSize(true);
        return actionToolbar;
    }

    /**
     * 创建添加按钮
     *
     * @param apiInfo
     * @return
     */
    public AddAction createAddAction(ApiInfo apiInfo) {
        AddAction addAction = new AddAction();
        addAction.setAction(e -> {
            // 弹出连接配置窗口
            ApiKeySettingsDialog apiKeySettingsDialog = new ApiKeySettingsDialog(project, apiInfo, this);
            apiKeySettingsDialog.show();
        });
        return addAction;
    }

    /**
     * 创建移除按钮
     *
     * @return
     */
    private DeleteAction createDeleteAction(ApiInfo apiInfo) {
        if (apiInfo != null) {
            this.apiInfo = apiInfo;
        }
        DeleteAction deleteAction = new DeleteAction();
        deleteAction.setAction(e -> {
            // 弹出删除确认对话框
            ConfirmDialog removeConnectionDialog = new ConfirmDialog(
                    project,
                    "Confirm",
                    "Are you sure you want to delete this API setting?",
                    actionEvent -> {
                        if (this.apiInfo != null) {
                            // connection列表中移除
                            propertyUtil.removeConnection(this.apiInfo);
                            this.apiInfo = null;
                            createListPanel();
                        }
                    });
            removeConnectionDialog.show();
        });
        return deleteAction;
    }

    /**
     * 创建截图按钮
     *
     * @return
     */
    public ScreenshotAction createScreenshotAction() {
        ActionManager actionManager = ActionManager.getInstance();
        AnAction action = actionManager.getAction("com.oeong.action.ScreenshotAction");
        if (action == null) {
            actionManager.registerAction("com.oeong.action.ScreenshotAction", action, PluginId.findId("com.oeong.idea-tools"));
        }
        ScreenshotAction screenshotAction = (ScreenshotAction) action;
        screenshotAction.setAction(e -> {
            startScreenshot();
        });
        screenshotAction.addTextOverride(ActionPlaces.TOOLWINDOW_TOOLBAR_BAR, "Screenshot(drag the mouse to select the screenshot area, ESC key to exit, " +
                "enter key to confirm the screenshot area, after the screenshot is completed, " +
                "it will be saved to the clipboard)");
//        KeyStroke firstKeyStroke = KeyStroke.getKeyStroke("alt A");
//        KeyboardShortcut keyboardShortcut = new KeyboardShortcut(firstKeyStroke, null);
//        if (screenshotRegisterFlag) {
//            this.register(screenshotAction, keyboardShortcut);
//            screenshotRegisterFlag = false;
//        }
        return screenshotAction;
    }

    /**
     * 创建ocr按钮
     *
     * @return
     */
    public OcrAction createOcrAction() {
        ActionManager actionManager = ActionManager.getInstance();
        AnAction action = actionManager.getAction("com.oeong.action.OcrAction");
        if (action == null) {
            actionManager.registerAction("com.oeong.action.OcrAction", action, PluginId.findId("com.oeong.idea-tools"));
        }
        OcrAction ocrAction = (OcrAction) action;
        ocrAction.setAction(e -> {
            // 弹出Ocr窗口
            OcrDialog ocrDialog = new OcrDialog(project, this);
            ocrDialog.show();
        });
//        KeyStroke firstKeyStroke = KeyStroke.getKeyStroke("control alt A");
//        KeyStroke secondKeyStroke = KeyStroke.getKeyStroke("C");
//        KeyboardShortcut keyboardShortcut = new KeyboardShortcut(firstKeyStroke, secondKeyStroke);
//        if (ocrRegisterFlag) {
//            this.register(ocrAction, keyboardShortcut);
//            ocrRegisterFlag = false;
//        }
        return ocrAction;
    }

//    public void register(CustomAction action, KeyboardShortcut keyboardShortcut) {
//        ShortcutSet[] shortcutSets = {new CustomShortcutSet(keyboardShortcut)};
//        ShortcutSet shortcutSet = new CompositeShortcutSet(shortcutSets);
//        action.registerCustomShortcutSet(shortcutSet, container);
//        // 获取Keymap的实例
//        Keymap keymap = KeymapManager.getInstance().getActiveKeymap();
//        // 为Action添加快捷键
//        ActionManager actionManager = ActionManager.getInstance();
//        actionManager.getAction(action.getActionId());
//        keymap.addShortcut(action.getActionId(), keyboardShortcut);
//    }

    public Map<String, ApiInfo> getConnectionMap() {
        Map<String, ApiInfo> stringConnectionInfoMap = new HashMap<>();
        List<ApiInfo> connections = propertyUtil.getConnections();
        for (ApiInfo connection : connections) {
            stringConnectionInfoMap.put(connection.getId(), connection);
        }
        return stringConnectionInfoMap;
    }

    /**
     * 保存或修改连接
     *
     * @param apiInfo
     */
    public void saveOrEditConnectionInfo(ApiInfo apiInfo) {
        Boolean global = apiInfo.getDefaultFlag();
        Map<String, ApiInfo> connectionMap = this.getConnectionMap();
        ApiInfo globalConnection = propertyUtil.getGlobalConnection();
        //没有全局配置的连接或者是没有连接
        if (global && (globalConnection != null || connectionMap == null)) {
            //如果是配置了全局,将其它所有全局都清除
            globalConnection.setDefaultFlag(Boolean.FALSE);
            propertyUtil.saveConnection(globalConnection);
        }
        propertyUtil.saveConnection(apiInfo);
    }

    /**
     * 连接名称有无重复
     *
     * @param connectionName
     * @return true 没有重复 ; false 重复
     */
    public boolean checkApiName(String connectionName, ApiInfo apiInfo) {
        boolean flag = true;
        List<ApiInfo> connections = propertyUtil.getConnections();
        for (ApiInfo connection : connections) {
            String name = connection.getName();
            if (connectionName.equals(name)) {
                if (apiInfo == null || !connection.getId().equals(apiInfo.getId())) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    /**
     * 创建一个连接右键菜单
     *
     * @return
     */
    private ActionPopupMenu createConnectionPopupMenu(ApiInfo apiInfo, JScrollPane connectionListPanel) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(createAddAction(apiInfo));
        actionGroup.add(createDeleteAction(apiInfo));
        ActionPopupMenu menu = ActionManager.getInstance().createActionPopupMenu(ActionPlaces.POPUP, actionGroup);
        menu.setTargetComponent(connectionListPanel);
        return menu;
    }

    public void createListPanel() {
        //添加连接显示
        //获取DefaultListModel
        Map<String, ApiInfo> connectionMap = this.getConnectionMap();
        DefaultListModel<ApiInfo> connectionModel = this.listToModel(connectionMap);
        connectionJList = new JBList<>();
        // 禁止多选
        connectionJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        connectionJList.setModel(connectionModel);

        connectionJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (e.getButton() == MouseEvent.BUTTON3) {
                    ApiInfo connection = (ApiInfo) connectionJList.getSelectedValue();
                    if (connection != null) {
                        createConnectionPopupMenu(connection, connectionListPanel).getComponent().show(connectionJList, x, y);
                    }
                }
            }
        });

        connectionJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (connectionJList != null) {
                    //获取被选中项的Api
                    apiInfo = (ApiInfo) connectionJList.getSelectedValue();
                }
            }
        });
        if (connectionJList != null) {
            connectionJList.setSelectedIndex(0);
        }
        //设置jblist的布局
        connectionJList.setLayoutOrientation(JList.VERTICAL_WRAP);
        if (connectionListPanel == null) {
            connectionListPanel = new JBScrollPane();
        }

        connectionListPanel.setViewportView(connectionJList);
        container.add(connectionListPanel, BorderLayout.CENTER);
        container.updateUI();
    }

    private DefaultListModel<ApiInfo> listToModel(Map<String, ApiInfo> connectionMap) {
        DefaultListModel<ApiInfo> titleListModel = new DefaultListModel<>();
        List<ApiInfo> apiInfoList = new ArrayList<>();
        if (connectionMap != null) {
            for (String id : connectionMap.keySet()) {
                apiInfoList.add(connectionMap.get(id));
            }
            for (int i = 0; i < apiInfoList.size(); i++) {
                titleListModel.add(i, apiInfoList.get(i));
            }
        }
        return titleListModel;
    }

    public ApiInfo getConConnectionDefault() {
        ApiInfo globalConnection = propertyUtil.getGlobalConnection();
        if (globalConnection == null) {
            if (connectionJList != null) {
                int selectedIndex = connectionJList.getSelectedIndex();
                Map<String, ApiInfo> connectionMap = this.getConnectionMap();
                return connectionMap.get(selectedIndex);
            }
        }
        return globalConnection;
    }
}