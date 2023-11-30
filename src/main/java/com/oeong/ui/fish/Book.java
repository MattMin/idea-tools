package com.oeong.ui.fish;

import com.intellij.icons.AllIcons;
import com.intellij.internal.statistic.eventLog.util.StringUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.oeong.notice.Notifier;
import com.oeong.service.BookService;
import com.oeong.service.ReadingProgressDao;
import com.oeong.vo.BookData;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/11/24 14:48
 */
public class Book {
    // Default Pattern
    private static final Pattern CHAPTER_PATTERN = Pattern.compile("^\\s*[第卷][0123456789一二三四五六七八九十零〇百千两]*[章回部节集卷].*");
    // 表头内容
    public static String[] head = {"文章名称", "最新章节", "链接"};
    // 表格内容
    public static DefaultTableModel tableModel = new DefaultTableModel(null, head);
    // 是否切换了书本（是否点击了开始阅读按钮）
    public static boolean isReadClick = false;
    // 阅读进度持久化
    static ReadingProgressDao readingProgressDao = ReadingProgressDao.getInstance();
    // 全局模块对象
    public Project project;
    //书籍本地导入地址
    public String importBookPath;
    // 窗口
    public JPanel mainPanel;
    // 表格选中的书名
    public String valueAtName;
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
    private JPanel textContentPanel;
    // 最新章节临时存储
    private String chapter;
    // 最新章节临时存储
    private List<String> bookChapterList = new ArrayList<>();
    // 书籍列表持久化
    private BookService bookService = BookService.getInstance();
    // 当前阅读书籍
    private BookData bookDataReading;


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
                    }
                });

        // 开始阅读
        openBook.addActionListener(e -> {

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
            valueAtName = bookTable.getValueAt(selectedRow, 0).toString();

            // 执行开始阅读
            new StartReading().execute();

            // 阅读进度持久化
            readingProgressDao.loadState(readingProgressDao);
        });

        // 上一章节跳转
        btnOn.addActionListener(e -> {
            mainPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            if (readingProgressDao.chapters.size() == 0 || readingProgressDao.nowChapterIndex == 0) {
                Notifier.notifyError("已经是第一章了");
                // 恢复默认鼠标样式
                mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            readingProgressDao.nowChapterIndex = readingProgressDao.nowChapterIndex - 1;
            // 加载阅读信息
            new LoadChapterInformation().execute();

            // 阅读进度持久化
            readingProgressDao.loadState(readingProgressDao);
        });

        // 下一章跳转
        underOn.addActionListener(e -> {
            // 等待鼠标样式
            mainPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            if (readingProgressDao.chapters.size() == 0 || readingProgressDao.nowChapterIndex == readingProgressDao.chapters.size() - 1) {
                Notifier.notifyError("已经是最后一章了");
                // 恢复默认鼠标样式
                mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return;
            }

            // 章节下标加一
            readingProgressDao.nowChapterIndex = readingProgressDao.nowChapterIndex + 1;

            // 加载阅读信息
            new LoadChapterInformation().execute();

            // 阅读进度持久化
            readingProgressDao.loadState(readingProgressDao);
        });

        // 章节跳转
        jumpButton.addActionListener(e -> {
            // 等待鼠标样式
            mainPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            // 根据下标跳转
            readingProgressDao.nowChapterIndex = chapterList.getSelectedIndex();

            if (readingProgressDao.chapters.size() == 0 || readingProgressDao.nowChapterIndex < 0) {
                Notifier.notifyError("未知章节");
                // 恢复默认鼠标样式
                mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return;
            }

            // 加载阅读信息
            new LoadChapterInformation().execute();

            // 阅读进度持久化
            readingProgressDao.loadState(readingProgressDao);
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

    public void init() {
        // 初始化表格
        HashMap<String, BookData> bookData = bookService.getBookData();
        if (bookData != null) {
            for (Map.Entry<String, BookData> entry : bookData.entrySet()) {
                BookData data = entry.getValue();
                tableModel.addRow(new Object[]{data.getBookName(), data.getChapter(), data.getBookLink()});
            }
        }
        bookTable.setModel(tableModel);
        bookTable.setEnabled(true);

        if (readingProgressDao.bookName != null) {
            bookTable.setSelectionMode(tableModel.findColumn(readingProgressDao.bookName));
        }

        if (readingProgressDao.chapters != null) {
            List<String> chapters = readingProgressDao.chapters;
            for (String chapter : chapters) {
                chapterList.addItem(chapter);
            }
        }

        if (readingProgressDao.textContent != null) {
            textContent.setText(readingProgressDao.textContent);
        }

        if (chapterList.getItemCount() != 0) {
            int nowChapterIndex = readingProgressDao.nowChapterIndex;
            chapterList.setSelectedIndex(nowChapterIndex);
        }


        // 设置表格内容大小
        tablePane.setPreferredSize(new Dimension(-1, 30));
        // 页面滚动步长
        JScrollBar jScrollBar = new JScrollBar();
        // 滚动步长为8
        jScrollBar.setMaximum(15);
        paneTextContent.setVerticalScrollBar(jScrollBar);

        // 阅读按钮
        openBook.setToolTipText("开始阅读");
        openBook.setIcon(AllIcons.Actions.Execute);
        // 上一章
        btnOn.setToolTipText("上一章");
        btnOn.setIcon(AllIcons.Actions.ArrowCollapse);
        // 下一章
        underOn.setToolTipText("下一章");
        underOn.setIcon(AllIcons.Actions.ArrowExpand);
        // 跳转
        jumpButton.setToolTipText("跳转");
        jumpButton.setIcon(AllIcons.Vcs.Push);
    }

    /**
     * 书籍导入
     */
    private void applyImportBook() {

        readingProgressDao.loadState(readingProgressDao);
        readingProgressDao.importPath = importBookPath;

        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(readingProgressDao.importPath);

        assert file != null;

        if (!this.importBook(file)) {
            Notifier.notifyError("书籍导入失败");
            return;
        }
        // 获取选中行数据
        int selectedRow = bookTable.getSelectedRow();

        if (selectedRow < 0) {
            Notifier.notifyError("还没有选择要读哪本书");
            // 恢复默认鼠标样式
            mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            return;
        }

        // 获取书籍链接
        valueAtName = bookTable.getValueAt(selectedRow, 0).toString();
        // 执行开始阅读
        new StartReading().execute();

        // 阅读进度持久化
        readingProgressDao.loadState(readingProgressDao);

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


        if (StringUtil.isEmpty(extension)) {
            return false;
        }

        // 获取文件路径
        String filePath = file.getPath();

        if (StringUtil.isEmpty(filePath)) {
            return false;
        }

        // 存储书籍信息
        Map<String, String> bookMap = new HashMap<>(16);

        // 存储目录信息
        List<String> chapterList = new ArrayList<>(16);
        // 执行书籍解析
        try {
            assert extension != null;
            if (extension.equals("txt") || extension.equals("TXT")) {
                bookMap = this.parseTxt(filePath, chapterList);
            } else {
                Notifier.notifyError("格式不支持");
            }
        } catch (Exception e) {
            return false;
        }

        if (bookMap.isEmpty() || chapterList.isEmpty()) return false;

        // 存储书籍
        BookData bookData = new BookData();
        bookData.setBookName(name);
        bookData.setBookLink(filePath);
        bookData.setBookMap(bookMap);
        bookData.setChapter(chapter);
        bookData.setBookChapterList(bookChapterList);

        // 持久化书籍
        bookService.getBookData().put(name, bookData);

        //添加到表格
        tableModel.addRow(bookData2Array(bookData));
        bookTable.setModel(tableModel);
        return true;
    }

    /**
     * 解析本地 txt 文件为 Map 格式，K 为章节名称，Value 为章节内容
     *
     * @param filePath txt 文件路径
     * @return <章节，章节内容>
     */
    public Map<String, String> parseTxt(String filePath, List<String> chapterList) {

        Map<String, String> chapterMap = new LinkedHashMap<>();

        String fileCharset = this.getFileCharset(filePath);

        chapter = "";
        bookChapterList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), fileCharset))) {
            String line;
            StringBuilder contentBuilder = new StringBuilder();
            String title = null;

            while (true) {
                try {
                    if ((line = reader.readLine()) == null) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Matcher matcher = CHAPTER_PATTERN.matcher(line);
                if (matcher.find()) {
                    if (title != null) {
                        if (!bookChapterList.contains(title)) {
                            bookChapterList.add(title);
                            chapterList.add(title);
                        }
                        chapterMap.put(title, contentBuilder.toString());
                        contentBuilder.setLength(0);
                    }
                    title = line;
                } else {
                    contentBuilder.append(line);
                    contentBuilder.append(System.lineSeparator());
                }
            }

            if (title != null) {
                chapter = title;
                chapterMap.put(title, contentBuilder.toString());
                chapterList.add(title);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return chapterMap;
    }

    /**
     * 判断文本文件的字符集，文件开头三个字节表明编码格式。
     *
     * @param filePath 文件路径
     * @return 字符集
     */
    public String getFileCharset(String filePath) {
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

    public String[] bookData2Array(BookData noteData) {
        String[] raw = new String[3];
        raw[0] = noteData.getBookName();
        raw[1] = noteData.getChapter();
        raw[2] = noteData.getBookLink();
        return raw;
    }

    /**
     * 开始阅读
     */
    final class StartReading extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() {
            bookDataReading = bookService.getBookData().get(valueAtName);
            readingProgressDao.chapters.clear();
            readingProgressDao.chapters.addAll(bookChapterList);
            return null;
        }

        @Override
        protected void done() {
            // 清空章节信息
            readingProgressDao.nowChapterIndex = 0;

            // 清空下拉列表
            chapterList.removeAllItems();

            // 加载下拉列表
            for (String chapter : bookDataReading.getBookChapterList()) {
                chapterList.addItem(chapter);
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
            // 清空书本表格
            String chapter = readingProgressDao.chapters.get(readingProgressDao.nowChapterIndex);

            // 内容
            Map<String, String> bookMap = bookDataReading.getBookMap();

            readingProgressDao.textContent = bookMap.get(chapter);

            if (readingProgressDao.textContent == null) {
                Notifier.notifyError("章节内容为空");
                return null;
            }
            //将当前进度信息加入chunks中
            publish(chapter);
            return null;
        }

        @Override
        protected void process(List<String> chapters) {
            String chapter = chapters.get(0);
            // 章节内容赋值
            textContent.setText(readingProgressDao.textContent);
            // 设置下拉框的值
            chapterList.setSelectedItem(chapter);
            // 回到顶部
            textContent.setCaretPosition(1);
        }

        @Override
        protected void done() {
            // 恢复默认鼠标样式
            mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

}