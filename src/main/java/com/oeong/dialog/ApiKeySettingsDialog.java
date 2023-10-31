package com.oeong.dialog;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LoadingDecorator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.JBUI;
import com.oeong.enums.ApiServerTypeEnum;
import com.oeong.tools.AliyunTools;
import com.oeong.tools.ApiSettingManager;
import com.oeong.tools.BaiduTools;
import com.oeong.vo.ApiInfo;
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

import static com.oeong.enums.ApiServerTypeEnum.getConnectionTypeList;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/10/13 15:38
 */
public class ApiKeySettingsDialog extends DialogWrapper implements Disposable {
    private JTextField nameTextField;
    private JTextField apiKeyField;
    private JPasswordField apiSecretField;
    private JCheckBox defaultCheckBox;

    private int type = ApiServerTypeEnum.baidu.getType();


    private ApiInfo apiInfo;
    private ApiSettingManager apiSettingManager;
    private boolean editFlag = false;

    public ApiKeySettingsDialog(@Nullable Project project, ApiInfo apiInfo, ApiSettingManager apiSettingManager) {
        super(project);
        this.setTitle("API Key Settings");
        this.setSize(650, 240);
        this.apiInfo = apiInfo;
        this.myOKAction = new CustomOKAction();
        this.apiSettingManager = apiSettingManager;
        this.init();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        boolean newApiInfo = apiInfo == null;

        nameTextField = new JTextField(newApiInfo ? null : apiInfo.getName());
        nameTextField.setToolTipText("API Name");
        //api服务选择框
        JBRadioButton aliRadio = new JBRadioButton(ApiServerTypeEnum.aliyun.getDescription());
        JBRadioButton baiduRadio = new JBRadioButton(ApiServerTypeEnum.baidu.getDescription());
        //设置选中
        int typeSelect = newApiInfo ? type :apiInfo.getType();
        if (typeSelect == ApiServerTypeEnum.baidu.getType()){
            baiduRadio.setSelected(true);
        } else if (typeSelect == ApiServerTypeEnum.aliyun.getType()){
            aliRadio.setSelected(true);
        } else {
            baiduRadio.setSelected(true);
        }

        ButtonGroup group = new ButtonGroup();
        group.add(aliRadio);
        group.add(baiduRadio);

        // apiKey 输入框
        apiKeyField = new JTextField(newApiInfo ? null : apiInfo.getApiKey());
        apiKeyField.setToolTipText("API Key");

        // ApiSecret输入框
        apiSecretField = new JPasswordField(newApiInfo ? null : apiInfo.getApiSecret());
        apiSecretField.setToolTipText("API Secret");

        // 显示ApiSecret
        JCheckBox showApiSecretCheckBox = new JCheckBox("Show API Secret");
        showApiSecretCheckBox.setBorder(JBUI.Borders.emptyRight(10));
        showApiSecretCheckBox.setPreferredSize(new Dimension(160, 12));
        showApiSecretCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                apiSecretField.setEchoChar((char) 0);
            } else {
                apiSecretField.setEchoChar('*');
            }
        });

        // 设为默认的api设置
        defaultCheckBox = new JCheckBox("As Default API Setting");
        defaultCheckBox.setSelected(!newApiInfo && apiInfo.getDefaultFlag());
        defaultCheckBox.setBorder(JBUI.Borders.emptyRight(10));
        defaultCheckBox.setPreferredSize(new Dimension(160, 12));

        JTextPane testResult = new JTextPane();
        testResult.setMargin(JBUI.insetsLeft(10));
        testResult.setOpaque(false);
        testResult.setEditable(false);
        testResult.setFocusable(false);
        testResult.setAlignmentX(SwingConstants.LEFT);

        LoadingDecorator loadingDecorator = new LoadingDecorator(testResult, this, 0);
        // 测试连接按钮
        JButton testButton = new JButton("Test API");
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
                            String message = "Fail";
                            if (success){
                                message = "Success";
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
        JLabel connectionNameLabel = new JLabel("API Name:");
        connectionNameLabel.setPreferredSize(new Dimension(130, 12));
        connectionNameLabel.setBorder(JBUI.Borders.emptyLeft(10));

        JLabel apiKeyLabel = new JLabel("API Key:");
        apiKeyLabel.setBorder(JBUI.Borders.emptyLeft(10));
        apiKeyLabel.setPreferredSize(new Dimension(130, 12));


        JLabel apiSecretLabel = new JLabel("API Secret:");
        apiSecretLabel.setBorder(JBUI.Borders.emptyLeft(10));
        apiSecretLabel.setPreferredSize(new Dimension(130, 12));


        JLabel typeRadioRowLabel = new JLabel("API Server:");
        typeRadioRowLabel.setBorder(JBUI.Borders.emptyLeft(10));
        typeRadioRowLabel.setPreferredSize(new Dimension(130, 12));

        JPanel typeRadioRowPanel = new JPanel(new GridLayout());
        typeRadioRowPanel.add(typeRadioRowLabel);
        typeRadioRowPanel.add(baiduRadio);
        typeRadioRowPanel.add(aliRadio);


        JPanel connectionNameRowPanel = new JPanel(new BorderLayout());
        connectionNameRowPanel.add(connectionNameLabel, BorderLayout.WEST);
        connectionNameRowPanel.add(nameTextField, BorderLayout.CENTER);
        connectionNameRowPanel.add(defaultCheckBox, BorderLayout.EAST);

        JPanel apiKeyRowPanel = new JPanel(new BorderLayout());
        apiKeyRowPanel.add(apiKeyLabel, BorderLayout.WEST);
        apiKeyRowPanel.add(apiKeyField, BorderLayout.CENTER);
        JLabel emptyLabel = new JLabel();
        emptyLabel.setBorder(JBUI.Borders.emptyRight(160));
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
        if (typeSelect != ApiServerTypeEnum.aliyun.getType()){
            connectionSettingsPanel.add(apiKeyRowPanel);
        }
        connectionSettingsPanel.add(apiSecretRowPanel);


        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(connectionSettingsPanel, BorderLayout.NORTH);
        centerPanel.add(testConnectionSettingsPanel, BorderLayout.SOUTH);

        //添加单选框监听器
        aliRadio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    type = ApiServerTypeEnum.aliyun.getType();
                    connectionSettingsPanel.remove(apiKeyRowPanel);
                    centerPanel.updateUI();
                }
            }
        });

        baiduRadio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    type = ApiServerTypeEnum.baidu.getType();
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
        if (apiInfo != null){
            editFlag = true;
            type = apiInfo.getType();
        }
        if (!isTest) {
            String text = nameTextField.getText();
            if (StringUtils.isBlank(text)) {
                return new ValidationInfo("API Name can not be empty");
            } else {
                boolean b = apiSettingManager.checkApiName(text,apiInfo);
                if (!b){
                    return new ValidationInfo("API Name is exist");
                }
            }
        }
        if (type == 0) {
            return new ValidationInfo("API server is must be choose");
        }

        if (!getConnectionTypeList().contains(type)) {
            return new ValidationInfo("API server is illegal");
        }

        if (type == ApiServerTypeEnum.baidu.getType()) {
            if (StringUtils.isBlank(apiKeyField.getText())) {
                return new ValidationInfo("API key can not be empty");
            }
        }

        String apiSecret = new String(apiSecretField.getPassword());
        if (StringUtils.isEmpty(apiSecret)) {
            return new ValidationInfo("API Secret can not be empty");
        }
        return null;
    }


    public boolean getTestResult(String apiKey,String apiSecret){
        boolean success = false;
        if (type == ApiServerTypeEnum.baidu.getType()) {
            String auth = BaiduTools.getAuth(apiKey, apiSecret);
            if (auth != null) {
                success = true;

            }
        } else if (type == ApiServerTypeEnum.aliyun.getType()) {
            String send = AliyunTools.send(apiSecret, null);
            if (send != null) {
                JSONObject sendObj = JSONUtil.parseObj(send);
                Integer errorCode = sendObj.getInt("error_code");
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
                    ErrorDialog.show("connect API failed");
                    return;
                }
                // 保存或修改Api setting
                String connectionInfoId = UUID.randomUUID().toString();
                ApiInfo apiInfoNew = ApiInfo.builder()
                        .id(editFlag ? apiInfo.getId() : connectionInfoId)
                        .name(nameTextField.getText())
                        .type(type)
                        .apiKey(apiKeyField.getText())
                        .defaultFlag(defaultCheckBox.isSelected())
                        .apiSecret(apiSecret)
                        .build();
                apiSettingManager.saveOrEditConnectionInfo(apiInfoNew);
                apiSettingManager.createListPanel();
                close(CANCEL_EXIT_CODE);
            }
        }
    }
}
