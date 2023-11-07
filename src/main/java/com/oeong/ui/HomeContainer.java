package com.oeong.ui;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBMenu;
import com.intellij.ui.plaf.beg.IdeaMenuUI;
import com.oeong.notice.Notifier;
import groovy.util.logging.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import static com.oeong.ui.MenuAction.*;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class HomeContainer {

    private final JPanel c = new JPanel();

    private final Set<JBMenu> menus = new HashSet<>();

    private final ToolWindow toolWindow;

    private final Map<String,JPanel> subJPanels = new HashMap<>();

    private final JPanel parent = new JPanel(new BorderLayout());

    public HomeContainer(ToolWindow toolWindow)  {
        this.toolWindow = toolWindow;
        c.setLayout(new BorderLayout());
        JPanel barPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JMenuBar menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(800, 35));
        var tools = createMenu("\uD83D\uDD27",TOOLS);
        tools.setFont(new Font("Default", Font.BOLD,14));
        menus.add(tools);
        menuBar.add(tools);
        var fish = createMenu("\uD83D\uDC1F",FISH);
        fish.setFont(new Font("Default", Font.BOLD,14));
        menus.add(fish);
        menuBar.add(fish);
        var ai = createMenu("\uD83E\uDD16",AI);
        ai.setFont(new Font("Default", Font.BOLD,14));
        menus.add(ai);
        menuBar.add(ai);
        var dev = createMenu("\u200D\uD83D\uDCBB",DEV);
        dev.setFont(new Font("Default", Font.BOLD,14));
        menus.add(dev);
        menuBar.add(dev);
        var items = new ArrayList<MenuAction>();
        URL url = getClass().getResource("/com");
        if(url==null){
            Notifier.notifyError("加载自定义菜单类异常");
            return;
        }
        String path = url.getPath();
        if(path.startsWith("file:")){
            path=path.substring(5);
        }
        try(JarFile jarFile = new JarFile(URLDecoder.decode(path.substring(0,path.lastIndexOf("/com")-1),UTF_8))){
            var enu = jarFile.entries();
            while (enu.hasMoreElements()) {
                JarEntry jarEntry = enu.nextElement();
                String name = jarEntry.getName();
                if (name.endsWith(".class")) {
                    String className = name.substring(0,name.length()-6).replace("/", ".");
                    Class<?> myclass = getClass().getClassLoader().loadClass(className);
                    var interfaces = myclass.getInterfaces();
                    if(Arrays.asList(interfaces).contains(MenuAction.class)){
                        MenuAction menu = (MenuAction) myclass.getDeclaredConstructor().newInstance();
                        items.add(menu);
                    }
                }
            }
        } catch (Exception e){
            throw new RuntimeException(e);
//            return;
        }
        addMenu(items);
        barPanel.add(menuBar);
        c.add(barPanel, BorderLayout.NORTH);
        c.add(parent, BorderLayout.CENTER);
//        hide(parent);
    }

    private JBMenu createMenu(String icon,String text){
        JBMenu menu = new JBMenu();
        menu.setText(icon+text);
        menu.setFont(new Font("Default", Font.BOLD,14));
        menu.setToolTipText(text);
        menu.setUI(new IdeaMenuUI());
        return menu;
    }


    private void addMenu(List<MenuAction> menuActions){
        Map<String, List<MenuAction>> map = menuActions.stream().collect(Collectors.groupingBy(MenuAction::parent));
        for(String key: map.keySet()){
            var opt = menus.stream().filter(e -> key.equals(e.getToolTipText())).findFirst();
            if(opt.isEmpty()){
                continue;
            }
            List<MenuAction> list = map.get(key).stream()
                    .sorted(Comparator.comparingInt(MenuAction::order))
                    .toList();
            JBMenu menu = opt.get();
            list.forEach(ma->{
                var item = menu.add(ma.getName());
                item.setFont(new Font("Default", Font.PLAIN,13));
                item.setPreferredSize(new Dimension(100,35));
                item.addActionListener(e->{
                    hide(parent);
                    JPanel panel = subJPanels.get(ma.getName());
                    if(panel==null){
                        panel =  ma.getContainer(toolWindow.getProject());
                        subJPanels.putIfAbsent(ma.getName(),panel);
                    }
                    parent.add(panel,0);
                    parent.repaint();
                    parent.revalidate();
                    panel.setVisible(true);
                    panel.repaint();
                    panel.revalidate();
                    ma.action(e);
                });
            });
        }
    }
    private void hide(JPanel paren){
        paren.removeAll();
    }
    public JPanel getContentPanel() {
        return c;
    }

}