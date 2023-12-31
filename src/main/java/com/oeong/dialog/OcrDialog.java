package com.oeong.dialog;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LoadingDecorator;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.JBUI;
import com.oeong.enums.ApiServerTypeEnum;
import com.oeong.tools.AliyunTools;
import com.oeong.tools.ApiSettingManager;
import com.oeong.tools.BaiduTools;
import com.oeong.vo.ApiInfo;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.oeong.tools.ScreenshotTools.getClipboardImage;

;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/10/13 15:38
 */
public class OcrDialog extends DialogWrapper implements Disposable {
    private JTextField urlTextField;
    private JComboBox connectionComboBox;

    private int type = 1;

    private ApiSettingManager apiSettingManager;

    public OcrDialog(@Nullable Project project, ApiSettingManager apiSettingManager) {
        super(project);
        this.setTitle("Ocr");
        this.setSize(650, 240);
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

        //ocr图片来源选择框
        JBRadioButton urlRadio = new JBRadioButton("URL");
        JBRadioButton screenshotRadio = new JBRadioButton("Clipboard");
        screenshotRadio.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(urlRadio);
        group.add(screenshotRadio);

        // url 输入框
        urlTextField = new JTextField();
        urlTextField.setToolTipText("URL");

        // 连接下拉框
        connectionComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel();
        Map<String, ApiInfo> connectionMap = apiSettingManager.getConnectionMap();
        int selectedIndex = -1;
        if (connectionMap != null) {
            for (String id : connectionMap.keySet()) {
                ApiInfo apiInfo = connectionMap.get(id);
                defaultComboBoxModel.addElement(apiInfo);
                if (apiInfo.getDefaultFlag()) {
                    selectedIndex = defaultComboBoxModel.getIndexOf(apiInfo);
                }
            }
        }
        connectionComboBox.setModel(defaultComboBoxModel);
        if (selectedIndex != -1) {
            connectionComboBox.setSelectedIndex(selectedIndex);
        }
        // ocr结果
        JTextArea resultArea = new JTextArea();
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setMargin(JBUI.insetsLeft(10));
        resultArea.setOpaque(false);
        resultArea.setEditable(false);
        resultArea.setFocusable(false);
        resultArea.setAlignmentX(SwingConstants.LEFT);

        LoadingDecorator loadingDecorator = new LoadingDecorator(resultArea, this, 0);
        // ocr按钮
        JButton ocrButton = new JButton("OCR");
        //ocr图片来源单选框
        JLabel optionRadioRowLabel = new JLabel("Picture Source:");
        optionRadioRowLabel.setBorder(JBUI.Borders.emptyLeft(10));
        optionRadioRowLabel.setPreferredSize(new Dimension(130, 12));

        JPanel optionRadioRowPanel = new JPanel(new GridLayout());
        optionRadioRowPanel.add(optionRadioRowLabel);
        optionRadioRowPanel.add(screenshotRadio);
        optionRadioRowPanel.add(urlRadio);

        //url
        JLabel urlLabel = new JLabel("URL:");
        urlLabel.setBorder(JBUI.Borders.emptyLeft(10));
        urlLabel.setPreferredSize(new Dimension(130, 12));

        JPanel urlRowPanel = new JPanel(new BorderLayout());
        urlRowPanel.add(urlLabel, BorderLayout.WEST);
        urlRowPanel.add(urlTextField, BorderLayout.CENTER);

        //连接选择下拉框
        JLabel connectionLabel = new JLabel("API Server:");
        connectionLabel.setBorder(JBUI.Borders.emptyLeft(10));
        connectionLabel.setPreferredSize(new Dimension(130, 12));

        JPanel connectionRowPanel = new JPanel(new BorderLayout());
        connectionRowPanel.add(connectionLabel, BorderLayout.WEST);
        connectionRowPanel.add(connectionComboBox, BorderLayout.CENTER);

        //ocr 结果
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(ocrButton);
        Component loadingComponent = loadingDecorator.getComponent().getComponent(0);
        JTextArea component = (JTextArea) loadingComponent;
        int lineCount = component.getLineCount();
        JScrollPane ocrResultPanel = new JScrollPane();
        ocrResultPanel.setViewportView(loadingComponent);
//        String text = ((JTextArea) loadingComponent).getText();


        JPanel ocrPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(ocrPanel, BoxLayout.Y_AXIS);
        ocrPanel.setLayout(boxLayout);
        ocrPanel.add(optionRadioRowPanel);
        if (urlRadio.isSelected()) {
            ocrPanel.add(urlRowPanel);
        }
        ocrPanel.add(connectionRowPanel);
        ocrPanel.add(row);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(ocrPanel, BorderLayout.NORTH);
        centerPanel.add(ocrResultPanel, BorderLayout.CENTER);

        //添加单选框监听器
        urlRadio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    type = 0;
                    ocrPanel.remove(connectionRowPanel);
                    ocrPanel.remove(ocrResultPanel);
                    ocrPanel.add(urlRowPanel);
                    ocrPanel.add(connectionRowPanel);
                    ocrPanel.add(row);
                    ocrPanel.updateUI();
                }
            }
        });

        screenshotRadio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    type = 1;
                    ocrPanel.remove(urlRowPanel);
                    ocrPanel.updateUI();
                }
            }
        });
        ocrButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadingDecorator.startLoading(false);
                ReadAction.nonBlocking(() -> {
                    try {
                        String image = "";
                        String url = "";
                        boolean success = false;
                        if (!(type == 0 || type == 1)) {
                            resultArea.setText("");
                            //ocr图片来源选择框 没有选择
                            resultArea.append("Picture source is must be choose");
                        }

                        if (type == 0) {
                            //输入url
                            String urlText = urlTextField.getText();
                            if (urlText == null || "".equals(urlText)) {
                                resultArea.setText("");
                                resultArea.append("Url can not be empty when url is selected as the image source");
                            } else {
                                url = urlText;
                            }
                        } else if (type == 1) {
                            //从剪贴板获取图片
                            Image clipboardImage = getClipboardImage();
                            if (clipboardImage == null) {
                                resultArea.setText("");
                                //剪贴板没有图片
                                resultArea.append("There are no pictures in the clipboard");
                            } else {
                                BufferedImage bufimg = (BufferedImage) clipboardImage;
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                ImageIO.write(bufimg, "jpg", baos);
                                baos.flush();
                                byte[] by = baos.toByteArray();
                                // base64编码
                                Base64.Encoder encoder = Base64.getEncoder();
                                image = encoder.encodeToString(by);
                            }
                        }

                        ApiInfo connectionSelected = (ApiInfo) connectionComboBox.getSelectedItem();
                        Map<String, String> bodyMap = new HashMap<>();
                        if (connectionSelected == null) {
                            resultArea.setText("");
                            resultArea.append("Connection is must be choose");
                        } else {
                            int server = connectionSelected.getType();
                            String apiKey = connectionSelected.getApiKey();
                            String apiSecret = connectionSelected.getApiSecret();
                            if (server == ApiServerTypeEnum.baidu.getType()) {
                                bodyMap.put("image", image);
                                bodyMap.put("url", url);
                                if (!"".equals(image) || !"".equals(url)) {
                                    String sendResult = BaiduTools.send(apiKey, apiSecret, JSONUtil.toJsonStr(bodyMap));
                                    if (sendResult != null) {
                                        JSONObject jsonObject = JSONUtil.parseObj(sendResult);
                                        if (jsonObject != null) {
                                            JSONArray wordsResult = jsonObject.getJSONArray("words_result");
                                            if (wordsResult != null) {
                                                resultArea.setText("");
                                                for (Object obj : wordsResult) {
                                                    JSONObject word = (JSONObject) obj;
                                                    String words = word.getStr("words");
                                                    resultArea.append(words + "\r\n");
                                                }
                                                success = Boolean.TRUE;
                                                resultArea.setEditable(true);
                                                resultArea.setFocusable(true);
                                            }
                                        }
                                    }
                                }
                            } else if (server == ApiServerTypeEnum.aliyun.getType()) {
                                bodyMap.put("img", image);
                                bodyMap.put("url", url);
                                if (!"".equals(image) || !"".equals(url)) {
                                    String sendResult = AliyunTools.send(apiSecret, JSONUtil.toJsonStr(bodyMap));
                                    if (sendResult != null) {
                                        JSONObject resultObj = JSONUtil.parseObj(sendResult);
                                        JSONArray prismWordsArr = resultObj.getJSONArray("prism_wordsInfo");
                                        if (prismWordsArr != null) {
                                            resultArea.setText("");
                                            for (Object obj : prismWordsArr) {
                                                JSONObject wordObject = (JSONObject) obj;
                                                String word = wordObject.getStr("word");
                                                resultArea.append(word + "\r\n");
                                            }
                                        }
                                        success = Boolean.TRUE;
                                        resultArea.setEditable(true);
                                        resultArea.setFocusable(true);
                                    }
                                }
                            }
                        }

                        if (success) {
                            resultArea.setForeground(JBColor.GREEN);
                        } else {
                            resultArea.setForeground(JBColor.RED);
                        }
                        ocrPanel.updateUI();
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
        });


        return centerPanel;
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
            close(CANCEL_EXIT_CODE);
        }
    }
}
