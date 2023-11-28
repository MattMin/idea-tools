package com.oeong.vo;

import java.util.List;
import java.util.Map;

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

    // 内容
    private Map<String, String> bookMap;

    // 章节列表
    private List<String> bookChapterList;

    public BookData() {
    }

    public BookData(String bookName, String chapter, String author, String bookLink, Map<String, String> bookMap) {
        this.bookName = bookName;
        this.chapter = chapter;
        this.author = author;
        this.bookLink = bookLink;
        this.bookMap = bookMap;
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

    public List<String> getBookChapterList() {
        return bookChapterList;
    }

    public void setBookChapterList(List<String> bookChapterList) {
        this.bookChapterList = bookChapterList;
    }

    public Map<String, String> getBookMap() {
        return bookMap;
    }

    public void setBookMap(Map<String, String> bookMap) {
        this.bookMap = bookMap;
    }
}
