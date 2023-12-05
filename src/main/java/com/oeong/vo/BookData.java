package com.oeong.vo;

import java.util.List;

/**
 * 存放书籍信息
 */
public class BookData {

    // 名称
    private String bookName;

    // 最新章节
    private String chapter;

    // 作者
    private String author;

    // 链接
    private String bookLink;

    // 章节列表
    private List<ChapterInfo> bookChapterList;

    // 当前章节下标
    private int nowChapterIndex;

    // 是否是最后阅读
    private boolean readFlag;

    public BookData() {
    }

    public BookData(String bookName, String chapter, String author, String bookLink, int nowChapterIndex, boolean readFlag) {
        this.bookName = bookName;
        this.chapter = chapter;
        this.author = author;
        this.bookLink = bookLink;
        this.nowChapterIndex = nowChapterIndex;
        this.readFlag = readFlag;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookLink() {
        return bookLink;
    }

    public void setBookLink(String bookLink) {
        this.bookLink = bookLink;
    }

    public List<ChapterInfo> getBookChapterList() {
        return bookChapterList;
    }

    public void setBookChapterList(List<ChapterInfo> bookChapterList) {
        this.bookChapterList = bookChapterList;
    }

    public int getNowChapterIndex() {
        return nowChapterIndex;
    }

    public void setNowChapterIndex(int nowChapterIndex) {
        this.nowChapterIndex = nowChapterIndex;
    }

    public boolean getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(boolean readFlag) {
        this.readFlag = readFlag;
    }
}
