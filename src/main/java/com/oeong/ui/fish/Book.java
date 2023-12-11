package com.oeong.ui.fish;

import com.intellij.icons.AllIcons;
import com.intellij.internal.statistic.eventLog.util.StringUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.table.JBTable;
import com.oeong.notice.Notifier;
import com.oeong.service.BookService;
import com.oeong.vo.BookData;
import com.oeong.vo.ChapterInfo;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/11/24 14:48
 */
public class Book {
    // TODO: 2023/12/6
    /**
     * 1 *上下拖动
     * 2 *监听表格异动
     * 3 *按钮文字提示
     * 4 *openbook 按钮修改
     * 5 书籍选择框修改为没有输入框
     * 6 *格式化框需要可以自适应
     * 7 *yaml需要添加插件依赖
     * 8 *书籍缓存只缓存一章
     * 9 *书籍读取方式修改为按行读取
     */
    // Default Pattern
    private static final Pattern CHAPTER_PATTERN = Pattern.compile("^\\s*[第卷][0123456789一二三四五六七八九十零〇百千两]*[章回部节集卷].*");
    // 表头内容
    public static String[] head = {"", "文章名称", "最新章节", "链接"};
    // 表格内容
    public static DefaultTableModel tableModel = new DefaultTableModel(null, head);
    // 是否切换了书本（是否点击了开始阅读按钮）
    public static boolean isReadClick = false;
    // 全局模块对象
    public Project project;
    //书籍本地导入地址
    public String importBookPath;
    // 窗口
    public JPanel mainPanel;
    // 表格选中的书名
    public String valueAtName;
    // 分隔
    private JBSplitter splitterContainer;
    // 导入本地书籍
    private TextFieldWithBrowseButton selectFile;
    // 上一章按钮
    private JButton btnOn;
    // 下一章按钮
    private JButton underOn;
    // 章节跳转按钮
    private JButton jumpButton;
    // 章节内容
    private JTextArea textContent;
    // 章节内容外部框
    private JScrollPane paneTextContent;
    // 章节目录下拉列表
    private JComboBox<String> chapterList;
    // 表格外围
    private JScrollPane tablePane;
    // 书本表格
    private JTable bookTable;
    // 打开按钮
    private JButton openBook;
    // 最新章节临时存储
    private String chapter;
    // 书籍列表持久化
    private BookService bookService = BookService.getInstance();
    // 当前阅读书籍
    private BookData bookDataReading;
    // 当前章节内容
    private String chapterContent;


    public Book(Project project) {
        this.project = project;
        init();
        // 书籍选择
        selectFile.addBrowseFolderListener("选择书籍文件", null, null,
                new FileChooserDescriptor(true, false, false,
                        false, false, false) {
                    @Override
                    public void validateSelectedFiles(VirtualFile @NotNull [] files) {

                        if (files.length == 0) {
                            return;
                        }
                        VirtualFile file = files[0];
                        importBookPath = file.getPath();
                        // 导入书籍
                        applyImportBook();
                        selectFile.getTextField().setText("");
                    }
                });

        // 上一章节跳转
        btnOn.addActionListener(e -> {
            mainPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            if (bookDataReading.getBookChapterList().size() == 0 || bookDataReading.getNowChapterIndex() == 0) {
                Notifier.notifyError("已经是第一章了");
                // 恢复默认鼠标样式
                mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            bookDataReading.setNowChapterIndex(bookDataReading.getNowChapterIndex() - 1);
            // 加载阅读信息
            new LoadChapterInformation().execute();

            // 阅读进度持久化
            bookService.getBookData().put(valueAtName, bookDataReading);
        });

        // 下一章跳转
        underOn.addActionListener(e -> {
            // 等待鼠标样式
            mainPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            List<ChapterInfo> bookChapterList = bookDataReading.getBookChapterList();
            int nowChapterIndex = bookDataReading.getNowChapterIndex();
            if (bookChapterList.size() == 0 || nowChapterIndex == bookChapterList.size() - 1) {
                Notifier.notifyError("已经是最后一章了");
                // 恢复默认鼠标样式
                mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            // 章节下标加一
            bookDataReading.setNowChapterIndex(nowChapterIndex + 1);

            // 加载阅读信息
            new LoadChapterInformation().execute();

            // 阅读进度持久化
            bookService.getBookData().put(valueAtName, bookDataReading);
        });

        // 章节跳转
        jumpButton.addActionListener(e -> {
            // 等待鼠标样式
            mainPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            // 根据下标跳转
            bookDataReading.setNowChapterIndex(chapterList.getSelectedIndex());

            if (bookDataReading.getBookChapterList().size() == 0 || bookDataReading.getNowChapterIndex() < 0) {
                Notifier.notifyError("未知章节");
                // 恢复默认鼠标样式
                mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return;
            }

            // 加载阅读信息
            new LoadChapterInformation().execute();

            // 阅读进度持久化
            bookService.getBookData().put(valueAtName, bookDataReading);
        });

//        // 阅读滚动
//        paneTextContent.getVerticalScrollBar().addAdjustmentListener(e -> {
//            int textWinIndex = paneTextContent.getVerticalScrollBar().getValue();
//            if (!(textWinIndex <= 0)) {
//                readSubscriptDao.homeTextWinIndex = textWinIndex;
//                // 阅读进度持久化
//                readSubscriptDao.loadState(readSubscriptDao);
//            }
//        });
    }

    private static List<String> getChapters(List<ChapterInfo> chapterInfos) {
        List<String> chapters = new ArrayList<>();
        for (ChapterInfo chapterInfo : chapterInfos) {
            chapters.add(chapterInfo.getChapterName());
        }
        return chapters;
    }

    /**
     * 判断文本文件的字符集，文件开头三个字节表明编码格式。
     *
     * @param filePath 文件路径
     * @return 字符集
     */
    public static String getFileCharset(String filePath) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
            bis.mark(100);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                bis.close();
                return charset; // 文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; // 文件编码为 Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; // 文件编码为 Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; // 文件编码为 UTF-8
                checked = true;
            }
            bis.reset();
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0) {
                        break;
                    }
                    if (0x80 <= read && read <= 0xBF) {// 单独出现BF以下的，也算是GBK
                        break;
                    }
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {// 双字节 (0xC0 - 0xDF)
                            // (0x80 - 0xBF),也可能在GB编码内
                            continue;
                        } else {
                            break;
                        }
                    } else if (0xE0 <= read && read <= 0xEF) { // 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }

    private static int findRowIndexByValue(DefaultTableModel model, String value) {
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                Object cellValue = model.getValueAt(row, col);
                if (cellValue != null && cellValue.toString().equals(value)) {
                    return row;
                }
            }
        }
        // 返回 -1 表示未找到匹配的值
        return -1;
    }

    public void init() {
        mainPanel = new JBPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        JBLabel jbLabel = new JBLabel("本地书籍:");
        selectFile = new TextFieldWithBrowseButton();
        selectFile.setMaximumSize(new Dimension(150, 25));
        selectFile.setMinimumSize(new Dimension(50, 25));
        topPanel.add(jbLabel);
        topPanel.add(selectFile);

        splitterContainer = new JBSplitter(true, 0.2f);

        // 初始化表格
        tableModel = new DefaultTableModel(null, head);
        HashMap<String, BookData> bookData = bookService.getBookData();
        if (bookData != null) {
            for (Map.Entry<String, BookData> entry : bookData.entrySet()) {
                BookData data = entry.getValue();
                JButton jButton = new JButton();
                jButton.setText("openBook");
                tableModel.addRow(new Object[]{jButton, data.getBookName(), data.getChapter(), data.getBookLink()});
                if (data.getReadFlag()) {
                    bookDataReading = data;
                }
            }
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        btnOn = new JButton("上一章");
        btnOn.setMaximumSize(new Dimension(100, 25));
        btnOn.setMinimumSize(new Dimension(50, 25));
        underOn = new JButton("下一章");
        underOn.setMaximumSize(new Dimension(100, 25));
        underOn.setMinimumSize(new Dimension(50, 25));
        jumpButton = new JButton("跳转到指定章节");
        jumpButton.setMaximumSize(new Dimension(100, 25));
        jumpButton.setMinimumSize(new Dimension(50, 25));
        chapterList = new JComboBox();
        chapterList.setMaximumSize(new Dimension(250, 25));
        chapterList.setMinimumSize(new Dimension(100, 25));

        //设置表格样式
        bookTable = new JBTable();
        bookTable.setModel(tableModel);
        bookTable.setEnabled(true);
        setTableButton();
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int column = e.getColumn();
                if (column == 3) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        // 在此处执行你希望执行的操作
                        int selectedRow = bookTable.getSelectedRow();
                        DefaultTableModel model = (DefaultTableModel) e.getSource();
                        String bookName = (String) model.getValueAt(selectedRow, 1);
                        BookData bookDataSelect = bookService.getBookData().get(bookName);
                        bookDataSelect.setBookLink((String) model.getValueAt(selectedRow, 3));
                        bookService.getBookData().put(bookName, bookDataSelect);
                    }
                }
            }
        });

        textContent = new JBTextArea();
        if (!ObjectUtils.isEmpty(bookDataReading)) {
            //加载表格选中项
            bookTable.setSelectionMode(findRowIndexByValue(tableModel, bookDataReading.getBookName()));
            //加载章节列表
            List<ChapterInfo> chapters = bookDataReading.getBookChapterList();
            for (ChapterInfo chapter : chapters) {
                chapterList.addItem(chapter.getChapterName());
            }
            if (chapterList.getItemCount() != 0) {
                //加载章节选中项
                int nowChapterIndex = bookDataReading.getNowChapterIndex();
                chapterList.setSelectedIndex(nowChapterIndex);
            }
            ChapterInfo chapterInfo = bookDataReading.getBookChapterList().get(bookDataReading.getNowChapterIndex());
            getChapterContent(bookDataReading.getBookLink(), chapterInfo);
            textContent.setText(chapterContent);
        }

        tablePane = new JBScrollPane();
        // 设置表格内容大小
        tablePane.setPreferredSize(new Dimension(-1, 30));
        tablePane.setViewportView(bookTable);
        // 页面滚动步长
        JScrollBar jScrollBar = new JScrollBar();
        // 滚动步长为8
        jScrollBar.setMaximum(15);
        paneTextContent = new JBScrollPane();
        paneTextContent.setVerticalScrollBar(jScrollBar);

        // 阅读按钮
//        openBook.setToolTipText("开始阅读");
//        openBook.setIcon(AllIcons.Actions.Execute);
        // 上一章
        btnOn.setToolTipText("上一章");
        btnOn.setIcon(AllIcons.Actions.ArrowCollapse);
        // 下一章
        underOn.setToolTipText("下一章");
        underOn.setIcon(AllIcons.Actions.ArrowExpand);
        // 跳转
        jumpButton.setToolTipText("跳转");
        jumpButton.setIcon(AllIcons.Vcs.Push);


        splitterContainer.setFirstComponent(tablePane);
        textContent.setCaretPosition(0);
        textContent.setLineWrap(true);
        paneTextContent.setViewportView(textContent);
        paneTextContent.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        splitterContainer.setSecondComponent(paneTextContent);
        // 同步滚动步长
        paneTextContent.getVerticalScrollBar().setUnitIncrement(8);
        // 字体大小
        textContent.setFont(new Font("", Font.BOLD, 16));

        bottomPanel.add(btnOn);
        bottomPanel.add(underOn);
        bottomPanel.add(jumpButton);
        chapterList.setVisible(true);
        chapterList.updateUI();
        chapterList.setPreferredSize(new Dimension(-1, 30));
        bottomPanel.add(chapterList);
//        chapterList.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                // 等待鼠标样式
//                mainPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//
//                // 根据下标跳转
//                if (e.getStateChange() == bookDataReading.getNowChapterIndex()){
//                    return;
//                }
//                bookDataReading.setNowChapterIndex(e.getStateChange());
//                if (bookDataReading.getBookChapterList().size() == 0 || bookDataReading.getNowChapterIndex() < 0) {
//                    Notifier.notifyError("未知章节");
//                    // 恢复默认鼠标样式
//                    mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//                    return;
//                }
//
//                // 加载阅读信息
//                new LoadChapterInformation().execute();
//
//                // 阅读进度持久化
//                bookService.getBookData().put(valueAtName, bookDataReading);
//            }
//        });

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitterContainer, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setTableButton() {
        bookTable.getColumnModel().getColumn(0).setCellRenderer(new TableCellRendererButton());
        bookTable.getColumnModel().getColumn(0).setCellEditor(new TableCellEditorButton(e -> {

            // 等待鼠标样式
            mainPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            // 获取选中行数据
            int selectedRow = bookTable.getSelectedRow();

            if (selectedRow < 0) {
                Notifier.notifyError("还没有选择要读哪本书");
                // 恢复默认鼠标样式
                mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return;
            }

            // 获取书籍链接
            valueAtName = bookTable.getValueAt(selectedRow, 1).toString();

            // 执行开始阅读
            new StartReading().execute();

            // 阅读进度持久化
            BookData bookDataNew = bookService.getBookData().get(valueAtName);
            bookDataNew.setReadFlag(true);
            bookDataNew.setNowChapterIndex(0);
            bookService.getBookData().put(valueAtName, bookDataNew);
            bookDataReading = bookDataNew;
            ChapterInfo chapterInfo = bookDataReading.getBookChapterList().get(bookDataReading.getNowChapterIndex());
            getChapterContent(bookDataNew.getBookLink(), chapterInfo);
        }));
    }

    /**
     * 书籍导入
     */
    private void applyImportBook() {

        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(importBookPath);

        assert file != null;

        if (!this.importBook(file)) {
            Notifier.notifyError("书籍导入失败");
            return;
        }
    }

    /**
     * 导入书籍
     *
     * @param file 书籍
     * @return 是否导入成功，true：成功、false：失败
     */
    public boolean importBook(@NotNull VirtualFile file) {
        // 获取扩展名
        String extension = file.getExtension();

        // 获取文件名
        String name = file.getName();

        BookData bookDataFind = bookService.getBookData().get(name);
        if (bookDataFind != null) {
            Notifier.notifyError("书籍已存在");
            return false;
        }

        if (StringUtil.isEmpty(extension)) {
            return false;
        }

        // 获取文件路径
        String filePath = file.getPath();

        if (StringUtil.isEmpty(filePath)) {
            return false;
        }

        // 存储书籍信息
        List<ChapterInfo> characterList = new ArrayList<>(16);

        // 存储目录信息
        List<String> chapterList = new ArrayList<>(16);
        // 执行书籍解析
        try {
            assert extension != null;
            if (extension.equals("txt") || extension.equals("TXT")) {
                characterList = this.parseTxt(filePath, chapterList);
            } else {
                Notifier.notifyError("格式不支持");
            }
        } catch (Exception e) {
            return false;
        }

        if (characterList.isEmpty() || chapterList.isEmpty()) return false;

        // 存储书籍
        BookData bookData = new BookData();
        bookData.setBookName(name);
        bookData.setBookLink(filePath);
        bookData.setChapter(chapter);
        bookData.setBookChapterList(characterList);
        bookData.setNowChapterIndex(0);
        bookData.setReadFlag(false);

        // 持久化书籍
        bookService.getBookData().put(name, bookData);

        //添加到表格
        tableModel.addRow(bookData2Array(bookData));
        bookTable.setModel(tableModel);
        setTableButton();
        bookTable.updateUI();
        return true;
    }

    /**
     * 解析本地 txt 文件为 Map 格式，K 为章节名称，Value 为章节内容
     *
     * @param filePath txt 文件路径
     * @return <章节，章节内容>
     */
    public List<ChapterInfo> parseTxt(String filePath, List<String> chapterList) {
        //章节信息列表
        List<ChapterInfo> characterList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();
        long fileSize = 0;

        //读取路径，并创建read对象
        BufferedReader read = null;
        try {
            Path path = Paths.get(filePath);
            fileSize = Files.size(path);
            // 记录每一行字节大小
            int lineSize = 0;
            // 记录已经读取到的字节总大小
            int totalSize = 0;
            read = new BufferedReader(new FileReader(filePath));
            //创建字符串line，指向read.readLine()返回的字符串
            String line = "";
            while (true) {
                try {
                    if ((line = read.readLine()) == null) break;
                } catch (IOException e) {
                    Notifier.notifyError("读取文件失败");
                }
                lineSize = line.getBytes().length;
                Matcher matcher = CHAPTER_PATTERN.matcher(line);
                if (matcher.find()) {
                    if (!chapterList.contains(line)) {
                        indexList.add(totalSize);
                        chapterList.add(line);
                        chapter = line;
                    }
                }
                //每行有换行符 需要+2
                totalSize = totalSize + lineSize + 2;
            }
        } catch (Exception e) {
            Notifier.notifyError("读取文件失败");
        }


//        try {
//            Path path = Paths.get(filePath);
//            fileSize = Files.size(path);
//            // 记录每一行字节大小
//            int lineSize = 0;
//            // 记录已经读取到的字节总大小
//            int totalSize = 0;
//            lines = Files.readAllLines(path, Charset.forName(fileCharset));
//            for (int i = 0; i < lines.size(); i++) {
//                String line = lines.get(i);
//                lineSize = line.getBytes().length;
//                Matcher matcher = CHAPTER_PATTERN.matcher(line);
//                if (matcher.find()) {
//                    if (!chapterList.contains(line)) {
//                        indexList.add(totalSize);
//                        chapterList.add(line);
//                        chapter = line;
//                    }
//                }
//                //每行有换行符 需要+2
//                totalSize = totalSize + lineSize + 2;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        for (int i = 0; i < indexList.size(); i++) {
            ChapterInfo chapterInfoFirst = new ChapterInfo();
            chapterInfoFirst.setChapterName(chapterList.get(i));
            chapterInfoFirst.setStartLine(indexList.get(i));
            if (i + 1 < indexList.size()) {
                chapterInfoFirst.setEndLine(indexList.get(i + 1));
            } else {
                chapterInfoFirst.setEndLine((int) fileSize);
            }
            characterList.add(chapterInfoFirst);
        }
        return characterList;
    }

    public String[] bookData2Array(BookData noteData) {
        String[] raw = new String[4];
        raw[0] = "";
        raw[1] = noteData.getBookName();
        raw[2] = noteData.getChapter();
        raw[3] = noteData.getBookLink();
        return raw;
    }

//    public void getLinesList(BookData bookDataReading) {
//        Path path = Paths.get(bookDataReading.getBookLink());
//        String fileCharset = getFileCharset(bookDataReading.getBookLink());
//        try {
//            lineList = Files.readAllLines(path, Charset.forName(fileCharset));
//        } catch (IOException e) {
//            Notifier.notifyError("获取章节内容失败");
//        }
//    }

    public void getChapterContent(String filePath, ChapterInfo chapterInfo) {
        File file = new File(filePath);
        RandomAccessFile randomAccessFile = null;
        try {
//            BufferedReader read = new BufferedReader(new FileReader(filePath));
//            char[] byteArray = new char[(int) file.length()];
//            read.read(byteArray, chapterInfo.getStartLine(), chapterInfo.getEndLine());
            randomAccessFile = new RandomAccessFile(file, "r");
            byte[] byteArray = new byte[(int) file.length()];
            randomAccessFile.seek(chapterInfo.getStartLine());
            randomAccessFile.read(byteArray, chapterInfo.getStartLine(), chapterInfo.getEndLine());
            chapterContent = new String(byteArray, Charset.forName(getFileCharset(filePath))).trim();
            if ("".equals(chapterContent)) {
                Notifier.notifyError("章节内容为空");
            }
        } catch (Exception e) {
            Notifier.notifyError("读取章节内容失败");
        }
    }

    /**
     * 开始阅读
     */
    final class StartReading extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() {
            return null;
        }

        @Override
        protected void done() {

            // 清空下拉列表
            chapterList.removeAllItems();

            // 加载下拉列表
            for (ChapterInfo chapter : bookDataReading.getBookChapterList()) {
                chapterList.addItem(chapter.getChapterName());
            }
            // 设置书籍
            HashMap<String, BookData> bookData = bookService.getBookData();
            if (bookData != null) {
                for (Map.Entry<String, BookData> entry : bookData.entrySet()) {
                    BookData data = entry.getValue();
                    String bookName = data.getBookName();
                    if (data.getReadFlag()) {
                        data.setReadFlag(false);
                        bookService.getBookData().put(bookName, data);
                    }
                    if (bookName.equals(bookDataReading.getBookName())) {
                        data.setReadFlag(true);
                        bookService.getBookData().put(bookName, data);
                    }
                }
            }

            // 解析当前章节内容
            new LoadChapterInformation().execute();

            // 书本已切换
            isReadClick = true;

            // 恢复默认鼠标样式
            mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * 加载章节信息
     */
    final class LoadChapterInformation extends SwingWorker<Void, String> {
        @Override
        protected Void doInBackground() {
            List<ChapterInfo> bookChapterList = bookDataReading.getBookChapterList();
            ChapterInfo chapterInfo = bookChapterList.get(bookDataReading.getNowChapterIndex());
            // 清空书本表格
            String chapter = chapterInfo.getChapterName();
            // 内容
            getChapterContent(bookDataReading.getBookLink(), chapterInfo);
            //将当前进度信息加入chunks中
            publish(chapter);
            return null;
        }

        @Override
        protected void process(List<String> chapters) {
            String chapter = chapters.get(0);
            // 章节内容赋值
            textContent.setText(chapterContent);
            // 设置下拉框的值
            chapterList.setSelectedItem(chapter);
            // 回到顶部
            textContent.setCaretPosition(0);
        }

        @Override
        protected void done() {
            // 恢复默认鼠标样式
            mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
}


class TableCellRendererButton implements TableCellRenderer {


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JButton button = new JButton("openBook");
        return button;
    }

}

class TableCellEditorButton extends DefaultCellEditor {

    private JButton btn;

    public TableCellEditorButton(ActionListener actionListener) {
        super(new JTextField());
        //设置点击一次就激活，否则默认好像是点击2次激活。
        this.setClickCountToStart(1);
        btn = new JButton("openBook");
        btn.addActionListener(actionListener);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        return btn;
    }


}


