package com.oeong.vo;

/**
 * @author : Zzw
 * @descriptions: 章节
 * @date: 2023/12/5 13:59
 */
public class ChapterInfo {

    // 名称
    private String chapterName;

    // 起始行
    private int startLine;

    // 结束行
    private int endLine;

    public ChapterInfo() {
    }

    public ChapterInfo(String chapterName, int startLine, int endLine) {
        this.chapterName = chapterName;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }
}
