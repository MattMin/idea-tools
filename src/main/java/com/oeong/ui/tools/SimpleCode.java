package com.oeong.ui.tools;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.jcef.JBCefBrowser;
import com.oeong.ui.ConsoleVirtualFile;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.intellij.plugins.markdown.ui.preview.html.MarkdownUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/9/18 10:26
 */
public class SimpleCode {
    private final JBCefBrowser browser = new JBCefBrowser();
    String responseBody;
    List<String> titleList;
    List<String> contentList;
    List<String> initTitleList;
    List<String> initContentList;
    private Project project;
    private JPanel container;
    private SearchTextField searchTextField;
    private JScrollPane titleListPanel;
    private JScrollPane contentPanel;
    private JList titleJList;
    private JPanel searchPanel;
    private JBSplitter splitter;
    private JPanel searchBoxPanel;

    public SimpleCode(Project project) {
        this.project = project;
        splitter = new JBSplitter(true, 0.2f);
        titleJList = new JBList();
        titleListPanel = new JBScrollPane(titleJList);
        contentPanel = new JBScrollPane();
        splitter.setFirstComponent(titleListPanel);
        splitter.setSecondComponent(contentPanel);
        container.add(splitter);

        initSimpleCode();
        titleJList.addListSelectionListener(new ListSelectionListener() {
            /**
             * Called whenever the value of the selection changes.
             *
             * @param e the event that characterizes the change.
             */
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (titleJList != null) {
                    //获取被选中项的index
                    int selectedIndex = titleJList.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        //拿到选中项标题对应的内容
                        String content = contentList.get(selectedIndex);
                        browser.loadHTML(content);
                    }
                }
            }
        });

    }

    public JPanel getContainer() {
        return container;
    }

    public void setContainer(JPanel container) {
        this.container = container;
    }

    public List<String> getTitleList() {
        return titleList;
    }

    public List<String> getContentList() {
        return contentList;
    }

    public JScrollPane getTitleListPanel() {
        return titleListPanel;
    }

    public void setTitleListPanel(JScrollPane titleListPanel) {
        this.titleListPanel = titleListPanel;
    }

    public JScrollPane getContentPanel() {
        return contentPanel;
    }

    public void setContentPanel(JScrollPane contentPanel) {
        this.contentPanel = contentPanel;
    }

    public JList getTitleJList() {
        return titleJList;
    }

    public void setTitleJList(JList titleJList) {
        this.titleJList = titleJList;
    }

    public void initSimpleCode() {
        //获取示例代码信息
        HttpGet get = new HttpGet("https://oeong.com/assets/file/demo-code.md");
        CloseableHttpClient client = HttpClientBuilder.create().useSystemProperties().build();
        CloseableHttpResponse response = null;
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000);
        get.setConfig(requestConfigBuilder.build());
        try {
            response = client.execute(get);
            responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            try (InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(responseBody.getBytes()));
                 BufferedReader br = new BufferedReader(isr)) {
                String line;
                StringBuilder contentBuilder = new StringBuilder();
                titleList = new ArrayList<>();
                contentList = new ArrayList<>();
                boolean contentFlag = false;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("```")) {
                        contentFlag = !contentFlag;
                    }
                    if (line.startsWith("# ") && !contentFlag) {
                        titleList.add(line.substring(2));
                        if (!ObjectUtils.isEmpty(contentBuilder)) {
                            contentList.add(contentBuilder.toString());
                            contentBuilder = new StringBuilder();
                        }
                    } else {
                        contentBuilder.append(line).append("\n");
                    }
                }
                contentList.add(contentBuilder.toString());
                //contentList内容转为html
                contentList = changeContentList(contentList);
                initContentList = contentList;
                initTitleList = titleList;
            } catch (Exception e) {
            }
        } catch (Exception ex) {
        }
    }

    public List<String> changeContentList(List<String> contentList) {
        List<String> contentMdList = new ArrayList<>();
        ConsoleVirtualFile md = new ConsoleVirtualFile("md", project);
        for (String content : contentList) {
            //显示内容
            String contentMd = MarkdownUtil.INSTANCE.generateMarkdownHtml(md, content, project);
            contentMdList.add(contentMd);
        }
        return contentMdList;
    }


    public JPanel getSimpleCodeContainer(Project project, String searchStr) {
        //添加搜索框
        JPanel searchBox = this.createSearchBox(project, searchStr);
        searchPanel.add(searchBox);
        this.getSearchResult(searchStr);
        //获取DefaultListModel
        DefaultListModel<String> titleListModel = this.listToModel(titleList);

        if (titleJList == null) {
            titleJList = new JBList<>();
        }
        titleJList.setModel(titleListModel);
        //设置jblist的布局
        titleJList.setLayoutOrientation(JList.VERTICAL);
        //设置选中的数据
        titleJList.setSelectedIndex(0);
        //初始化内容数据
        String content = contentList.get(0);
        browser.loadHTML(content);
        //获取content显示的区域
        contentPanel = this.getContentPanel();
        if (contentPanel == null) {
            contentPanel = new JBScrollPane();
        }
        contentPanel.setBorder(null);
        if (searchStr == null) {
            contentPanel.setViewportView(browser.getComponent());
        }
        //设置显示数量
        titleJList.setVisibleRowCount(5);
        //获取Title显示的区域
        if (titleListPanel == null) {
            titleListPanel = new JBScrollPane();
        }
        titleListPanel.setBorder(null);
        titleListPanel.setViewportView(titleJList);
        return container;
    }


    public JPanel createSearchBox(Project project, String searchStr) {
        if (searchTextField == null) {
            searchTextField = new SearchTextField();
        }
        searchTextField.setText(searchStr);
        searchTextField.addKeyboardListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String text = searchTextField.getText();
                    getSimpleCodeContainer(project, text);
                }
            }
        });
        if (searchBoxPanel == null) {
            searchBoxPanel = new JPanel();
        }
        searchBoxPanel.add(searchTextField);
        return searchBoxPanel;
    }

    private DefaultListModel<String> listToModel(List<String> titleList) {
        DefaultListModel<String> titleListModel = new DefaultListModel<>();
        for (int i = 0; i < titleList.size(); i++) {
            titleListModel.add(i, titleList.get(i));
        }
        return titleListModel;
    }

    public void getSearchResult(String searchStr) {
        List<String> resultList = new ArrayList<>();
        List<String> contentResultList = new ArrayList<>();
        if (searchStr != null && !"".equals(searchStr)) {
            String[] strArray = searchStr.split(" ");
            List<Pattern> patternList = new ArrayList<>();
            for (String str : strArray) {
                String regex = str.toLowerCase() + "+";
                // 创建模式对象
                Pattern p = Pattern.compile(regex);
                patternList.add(p);
            }
            for (int i = 0; i < initTitleList.size(); i++) {
                boolean flag = true;
                for (Pattern pattern : patternList) {
                    // 创建匹配器对象
                    Matcher m = pattern.matcher(initTitleList.get(i).toLowerCase());
                    flag = m.find();
                    if (!flag) {
                        break;
                    }
                }
                if (flag) {
                    resultList.add(initTitleList.get(i));
                    contentResultList.add(initContentList.get(i));
                }
            }
            titleList = resultList;
            contentList = contentResultList;
        } else {
            titleList = initTitleList;
            contentList = initContentList;
        }
    }

}
