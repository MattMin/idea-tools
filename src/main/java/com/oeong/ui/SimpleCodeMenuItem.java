package com.oeong.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.jcef.JBCefBrowser;
import com.oeong.ui.tools.SimpleCode;
import org.intellij.plugins.markdown.ui.preview.html.MarkdownUtil;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import javax.swing.plaf.TabbedPaneUI;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/9/15 9:17
 */
public class SimpleCodeMenuItem implements MenuAction {
    /**
     * 当前菜单名称
     *
     * @return
     */
    @Override
    public @NotNull String getName() {
        return "simple code";
    }

    /**
     * 父级菜单名称
     *
     * @return 对应 MenuAction.FISH,MenuAction.AI,MenuAction.DEV,MenuAction.TOOLS
     */
    @Override
    public @NotNull String parent() {
        return DEV;
    }

    /**
     * 子菜单排序
     *
     * @return 数字越大越靠后
     */
    @Override
    public int order() {
        return 0;
    }

    /**
     * 菜单点击动作
     *
     * @param event 事件对象
     */
    @Override
    public void action(ActionEvent event) {

    }

    /**
     * 子菜单对应的面板
     *
     * @param project 当前工程
     * @return 面板对象
     */
    @Override
    public @NotNull JPanel getContainer(Project project) {
        SimpleCode simpleCode = new SimpleCode(project);

        JTabbedPane tabbedPane = simpleCode.getTabbedPane();
        if (tabbedPane == null) {
            tabbedPane = new JBTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

        }
        TabbedPaneUI def = new MyTabbedPaneUI();
        tabbedPane.setUI(def);
        try {
            List<String> titleList = simpleCode.getTitleList();
            List<String> contentList = simpleCode.getContentList();
//            List<String> titleList = new ArrayList<>();
//            List<String> contentList = new ArrayList<>();
//            String[] responseArray = simpleCode.getResponseBody().split("# ");
//            for (String str : responseArray) {
//                if (!"".equals(str)){
//                    str = "# " + str;
//                    String strText = processor.process(str);
//                    Document parseHtml = Jsoup.parse(strText);
//                    Element title = parseHtml.select("h1").get(0);
//                    titleList.add(title.text());
//                    Elements codes = parseHtml.select("code");
//                    StringBuilder content = new StringBuilder();
//                    for (Element code : codes) {
//                        content.append(code.text());
//                    }
//                    contentList.add(content.toString());
//                }
//            }
            //填充数据到面板
            for (int i = 0; i < titleList.size(); i++) {
                String title = titleList.get(i);
                String content = contentList.get(i);
                ConsoleVirtualFile md = new ConsoleVirtualFile("md", project);
                String contentMd = MarkdownUtil.INSTANCE.generateMarkdownHtml(md, content, project);
                JBCefBrowser browser = new JBCefBrowser();
                browser.loadHTML(contentMd);
                JScrollPane jScrollPane = new JScrollPane();
                jScrollPane.setViewportView(browser.getComponent());
                tabbedPane.addTab(title, null, jScrollPane, title);
            }
        } catch (Exception e) {
            // todo
            e.printStackTrace();
        }
        JPanel container = simpleCode.getContainer();
//        tabbedPane.setUI(new MyTabbedPaneUI());
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        container.add(tabbedPane, -1);
        return container;
    }


    private List<String> elementsToList(Elements elements) {
        List<String> list = new ArrayList<>();
        for (Element element : elements) {
            list.add(element.text());
        }
        return list;
    }
}
