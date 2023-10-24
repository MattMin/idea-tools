package com.oeong.dialog;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LoadingDecorator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.JBUI;
import com.oeong.tools.AliyunTools;
import com.oeong.tools.BaiduTools;
import com.oeong.tools.ConnectionManager;
import com.oeong.ui.*;
import com.oeong.vo.ConnectionInfo;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.oeong.ui.ConnectionTypeEnum.getConnectionTypeList;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/10/13 15:38
 */
public class ApiKeySettingsDialog extends DialogWrapper implements Disposable {
    private JTextField nameTextField;
    private JTextField apiKeyField;
    private JPasswordField apiSecretField;
    private JCheckBox globalCheckBox;

    private int type = ConnectionTypeEnum.baidu.getType();


    private ConnectionInfo connection;
    private ConnectionManager connectionManager;

    public ApiKeySettingsDialog(@Nullable Project project, ConnectionInfo connectionInfo, ConnectionManager connectionManager) {
        super(project);
        this.setTitle("ApiKey Settings");
        this.setSize(650, 240);
        this.connection = connectionInfo;
        this.myOKAction = new CustomOKAction();
        this.connectionManager = connectionManager;
        this.init();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        boolean newConnection = connection == null;

        nameTextField = new JTextField(newConnection ? null : connection.getName());
        nameTextField.setToolTipText("Connection Name");
        //api服务选择框
        JBRadioButton aliRadio = new JBRadioButton(ConnectionTypeEnum.aliyun.getDescription());
        JBRadioButton baiduRadio = new JBRadioButton(ConnectionTypeEnum.baidu.getDescription());
        baiduRadio.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(aliRadio);
        group.add(baiduRadio);

        // apiKey 输入框
        apiKeyField = new JTextField(newConnection ? null : connection.getApiKey());
        apiKeyField.setToolTipText("ApiKey");

        // ApiSecret输入框
        apiSecretField = new JPasswordField(newConnection ? null : connection.getApiSecret());
        apiSecretField.setToolTipText("ApiSecret");

        // 显示ApiSecret
        JCheckBox showApiSecretCheckBox = new JCheckBox("Show ApiSecret");
        showApiSecretCheckBox.setBorder(JBUI.Borders.emptyRight(10));
        showApiSecretCheckBox.setPreferredSize(new Dimension(140, 12));
        showApiSecretCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                apiSecretField.setEchoChar((char) 0);
            } else {
                apiSecretField.setEchoChar('*');
            }
        });

        // 设为全局
        globalCheckBox = new JCheckBox("As Global");
        globalCheckBox.setSelected(!newConnection && connection.getGlobal());
        globalCheckBox.setBorder(JBUI.Borders.emptyRight(10));
        globalCheckBox.setPreferredSize(new Dimension(140, 12));

        JTextPane testResult = new JTextPane();
        testResult.setMargin(JBUI.insetsLeft(10));
        testResult.setOpaque(false);
        testResult.setEditable(false);
        testResult.setFocusable(false);
        testResult.setAlignmentX(SwingConstants.LEFT);

        LoadingDecorator loadingDecorator = new LoadingDecorator(testResult, this, 0);
        // 测试连接按钮
        JButton testButton = new JButton("Test Connection");
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ValidationInfo validationInfo = doValidate(true);
                if (validationInfo != null) {
                    ErrorDialog.show(validationInfo.message);
                } else {
                    String apiSecret;
                    String pwd = new String(apiSecretField.getPassword());
                    if (StringUtils.isEmpty(pwd)) {
                        apiSecret = null;
                    } else {
                        apiSecret = pwd;
                    }

                    loadingDecorator.startLoading(false);
                    ReadAction.nonBlocking(() -> {
                        try {
                            boolean success = getTestResult(apiKeyField.getText(), apiSecret);
                            String message = "测试失败";
                            if (success){
                                message = "测试成功";
                            }
                            testResult.setText(message);
                            if (success) {
                                testResult.setForeground(JBColor.GREEN);
                            } else {
                                testResult.setForeground(JBColor.RED);
                            }
                        } finally {
                            loadingDecorator.stopLoading();
                        }
                        return null;
                    }).submit(new ThreadPoolExecutor(
                            20,
                            100,
                            10,
                            TimeUnit.SECONDS,
                            new LinkedBlockingDeque<>(),
                            Executors.defaultThreadFactory(),
                            new ThreadPoolExecutor.AbortPolicy()
                    ));
                }
            }
        });
        JLabel connectionNameLabel = new JLabel("Connection Name:");
        connectionNameLabel.setPreferredSize(new Dimension(130, 12));
        connectionNameLabel.setBorder(JBUI.Borders.emptyLeft(10));

        JLabel apiKeyLabel = new JLabel("ApiKey:");
        apiKeyLabel.setBorder(JBUI.Borders.emptyLeft(10));
        apiKeyLabel.setPreferredSize(new Dimension(130, 12));


        JLabel apiSecretLabel = new JLabel("ApiSecret:");
        apiSecretLabel.setBorder(JBUI.Borders.emptyLeft(10));
        apiSecretLabel.setPreferredSize(new Dimension(130, 12));


        JLabel typeRadioRowLabel = new JLabel("ApiServer:");
        typeRadioRowLabel.setBorder(JBUI.Borders.emptyLeft(10));
        typeRadioRowLabel.setPreferredSize(new Dimension(130, 12));

        JPanel typeRadioRowPanel = new JPanel(new GridLayout());
        typeRadioRowPanel.add(typeRadioRowLabel);
        typeRadioRowPanel.add(baiduRadio);
        typeRadioRowPanel.add(aliRadio);


        JPanel connectionNameRowPanel = new JPanel(new BorderLayout());
        connectionNameRowPanel.add(connectionNameLabel, BorderLayout.WEST);
        connectionNameRowPanel.add(nameTextField, BorderLayout.CENTER);
        connectionNameRowPanel.add(globalCheckBox, BorderLayout.EAST);

        JPanel apiKeyRowPanel = new JPanel(new BorderLayout());
        apiKeyRowPanel.add(apiKeyLabel, BorderLayout.WEST);
        apiKeyRowPanel.add(apiKeyField, BorderLayout.CENTER);
        JLabel emptyLabel = new JLabel();
        emptyLabel.setBorder(JBUI.Borders.emptyRight(140));
        apiKeyRowPanel.add(emptyLabel, BorderLayout.EAST);

        JPanel apiSecretRowPanel = new JPanel(new BorderLayout());
        apiSecretRowPanel.add(apiSecretLabel, BorderLayout.WEST);
        apiSecretRowPanel.add(apiSecretField, BorderLayout.CENTER);
        apiSecretRowPanel.add(showApiSecretCheckBox, BorderLayout.EAST);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(testButton);
        JPanel testConnectionSettingsPanel = new JPanel(new GridLayout(2, 1));
        testConnectionSettingsPanel.add(row);
        testConnectionSettingsPanel.add(loadingDecorator.getComponent());


        JPanel connectionSettingsPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(connectionSettingsPanel, BoxLayout.Y_AXIS);
        connectionSettingsPanel.setLayout(boxLayout);
        connectionSettingsPanel.add(typeRadioRowPanel);
        connectionSettingsPanel.add(connectionNameRowPanel);
        connectionSettingsPanel.add(apiKeyRowPanel);
        connectionSettingsPanel.add(apiSecretRowPanel);


        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(connectionSettingsPanel, BorderLayout.NORTH);
        centerPanel.add(testConnectionSettingsPanel, BorderLayout.SOUTH);

        //添加单选框监听器
        aliRadio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    type = ConnectionTypeEnum.aliyun.getType();
                    connectionSettingsPanel.remove(apiKeyRowPanel);
                    centerPanel.updateUI();
                }
            }
        });

        baiduRadio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    type = ConnectionTypeEnum.baidu.getType();
                    connectionSettingsPanel.remove(apiSecretRowPanel);
                    connectionSettingsPanel.add(apiKeyRowPanel);
                    connectionSettingsPanel.add(apiSecretRowPanel);
                    centerPanel.updateUI();
                }
            }
        });


        return centerPanel;
    }

    /**
     * 校验数据
     *
     * @return 通过必须返回null，不通过返回一个 ValidationInfo 信息
     */
    @Nullable
    protected ValidationInfo doValidate(boolean isTest) {
        if (!isTest) {
            String text = nameTextField.getText();
            if (StringUtils.isBlank(text)) {
                return new ValidationInfo("Connection Name can not be empty");
            } else {
                boolean b = connectionManager.saveOrEditConnectionInfo(text);
                if (!b){
                    return new ValidationInfo("Connection Name is exist");
                }
            }
        }
        if (type == 0) {
            return new ValidationInfo("Api server is must be choose");
        }

        if (!getConnectionTypeList().contains(type)) {
            return new ValidationInfo("Api server is illegal");
        }

        if (type == ConnectionTypeEnum.baidu.getType()) {
            if (StringUtils.isBlank(apiKeyField.getText())) {
                return new ValidationInfo("Apikey can not be empty");
            }
        }

        String apiSecret = new String(apiSecretField.getPassword());
        if (StringUtils.isEmpty(apiSecret)) {
            return new ValidationInfo("ApiSecret can not be empty");
        }
        return null;
    }


    public boolean getTestResult(String apiKey,String apiSecret){
        boolean success = false;
        if (type == ConnectionTypeEnum.baidu.getType()) {
            String auth = BaiduTools.getAuth(apiKey, apiSecret);
            if (auth != null) {
                success = true;

            }
        } else if (type == ConnectionTypeEnum.aliyun.getType()) {
            String send = AliyunTools.send(apiSecret, null);
            if (send != null) {
                JSONObject sendObj = JSONObject.parseObject(send);
                Integer errorCode = sendObj.getInteger("error_code");
                if (errorCode != null && errorCode == 400) {
                    success = true;
                }
            }
        }

        return success;
    }

    /**
     * Usually not invoked directly, see class javadoc.
     */
    @Override
    public void dispose() {
        super.dispose();
    }


    /**
     * 自定义 ok Action
     */
    protected class CustomOKAction extends DialogWrapperAction {

        protected CustomOKAction() {
            super("OK");
            putValue(DialogWrapper.DEFAULT_ACTION, true);
        }

        @Override
        protected void doAction(ActionEvent e) {
            ValidationInfo validationInfo = doValidate(false);
            if (validationInfo != null) {
                ErrorDialog.show(validationInfo.message);
            } else {
                String apiSecret = new String(apiSecretField.getPassword());
                if (!getTestResult(apiKeyField.getText(),apiSecret)){
                    ErrorDialog.show("connection failed");
                    return;
                }
                // 保存或修改connection
                String connectionInfoId = UUID.randomUUID().toString();
                ConnectionInfo connectionInfo = ConnectionInfo.builder()
                        .id(connectionInfoId)
                        .name(nameTextField.getText())
                        .type(type)
                        .apiKey(apiKeyField.getText())
                        .global(globalCheckBox.isSelected())
                        .apiSecret(apiSecret)
                        .build();
                connectionManager.saveOrEditConnectionInfo(connectionInfo);
                connectionManager.createListPanel();
                close(CANCEL_EXIT_CODE);
            }
        }
    }
}
