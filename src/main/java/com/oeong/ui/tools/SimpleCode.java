package com.oeong.ui.tools;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.jcef.JBCefBrowser;
import com.oeong.ui.ConsoleVirtualFile;
import com.oeong.ui.MyTabbedPaneUI;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.intellij.plugins.markdown.ui.preview.html.MarkdownUtil;

import javax.swing.*;
import javax.swing.plaf.TabbedPaneUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/9/18 10:26
 */
public class SimpleCode {
    String responseBody;
    List<String> titleList;
    List<String> contentList;

    private JPanel container;
    private JTextField textField1;
    private JButton searchButton;
    private JEditorPane editorPane1;
    private JTabbedPane tabbedPane;


    public SimpleCode(Project project) {
        initSimpleCode();
        searchButton.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                // 获取到搜索框的数据
                String textField = textField1.getText();
                List<String> titleListSearch = new ArrayList<>();
                List<String> contentListSearch = new ArrayList<>();

                if (textField == null || "".equals(textField)) {
                    titleListSearch = titleList;
                    contentListSearch = contentList;
                } else {
                    for (int i = 0; i < titleList.size(); i++) {
                        String title = titleList.get(i);
                        if (title.toLowerCase().contains(textField.toLowerCase())) {
                            titleListSearch.add(titleList.get(i));
                            contentListSearch.add(contentList.get(i));
                        }
                    }
                }
                JTabbedPane tabbedPaneSearch = new JBTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
                TabbedPaneUI def = new MyTabbedPaneUI();
                tabbedPaneSearch.setUI(def);
                //填充数据到面板
                for (int i = 0; i < titleListSearch.size(); i++) {
                    String title = titleListSearch.get(i);
                    String content = contentListSearch.get(i);
                    ConsoleVirtualFile md = new ConsoleVirtualFile("md", project);
                    String contentMd = MarkdownUtil.INSTANCE.generateMarkdownHtml(md, content, project);
                    JBCefBrowser browser = new JBCefBrowser();
                    browser.loadHTML(contentMd);
                    JScrollPane jScrollPane = new JScrollPane();
                    jScrollPane.setViewportView(browser.getComponent());
                    tabbedPaneSearch.addTab(title,null,jScrollPane,title);
                }
                tabbedPaneSearch.setTabPlacement(JTabbedPane.LEFT);
                Component component = container.getComponent(0);
                container.removeAll();
                container.setLayout(new BorderLayout());
                container.add(component, BorderLayout.NORTH);
                container.add(tabbedPaneSearch, BorderLayout.CENTER);
                super.mouseClicked(e);
            }
        });
    }

    public JPanel getContainer() {
        return container;
    }

    public void setContainer(JPanel container) {
        this.container = container;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void setTabbedPane(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }


    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public List<String> getTitleList() {
        return titleList;
    }

    public void setTitleList(List<String> titleList) {
        this.titleList = titleList;
    }

    public List<String> getContentList() {
        return contentList;
    }

    public void setContentList(List<String> contentList) {
        this.contentList = contentList;
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
        //添加代理
        requestConfigBuilder.setProxy(new HttpHost("127.0.0.1", 7890));
        get.setConfig(requestConfigBuilder.build());
        try {
            response = client.execute(get);
            responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
//            //剑建一个FiLe对象，指定要读取的文件路轻
//            File file = new File("https://raw.githubusercontent.com/MattMin/idea-tools/dev/assets/demo-code.md");
//            //创建一FileInputstream.对象，将File对象作为签数传入
//            FileInputStream fis = null;
            try {
//                fis = new FileInputStream(file);
                //创建一个InputstreamReader对象，将FileInputstream对象作为袋数传入
                InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(responseBody.getBytes()));
                //创建一BufferedReader对象，将InputstreamReader对象作为鉴物传入
                BufferedReader br = new BufferedReader(isr);
                //BufferedReader的readLine()方法遂行读欺文件中容
                String line;
                StringBuilder contentBuilder = new StringBuilder();
                titleList = new ArrayList<>();
                contentList = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("# ")){
                        titleList.add(line.substring(2));
                        if (!ObjectUtils.isEmpty(contentBuilder)){
                            contentList.add(contentBuilder.toString());
                            contentBuilder = new StringBuilder();
                        }
                    }else {
                        contentBuilder.append(line).append("\n");
                    }
                }
                contentList.add(contentBuilder.toString());
                //关BufferedReader、InputstreamReaderFileInputstream对象
                br.close();
                isr.close();
//                fis.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
//            titleList = new ArrayList<>();
//            contentList = new ArrayList<>();
//            String[] responseArray = responseBody.split("# ");
//            //解析md文档
//            Markdown4jProcessor processor = new Markdown4jProcessor();
//            for (String str : responseArray) {
//                if (!"".equals(str)) {
//                    str = "# " + str;
//                    String strText = null;
//                    try {
//                        strText = processor.process(str);
//                    } catch (Exception ex) {
//                        throw new RuntimeException(ex);
//                    }
//                    Document parseHtml = Jsoup.parse(strText);
//                    Element title = parseHtml.select("h1").get(0);
//                    titleList.add(title.text());
//                    Elements codes = parseHtml.select("code");
//                    StringBuilder content = new StringBuilder();
//                    for (Element code : codes) {
//                        content.append(code.text()).append("\n");
//                    }
//                    contentList.add(content.toString());
//                }
//            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
