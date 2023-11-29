package com.oeong.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 阅读进度持久化
 */
@State(name = "ReadingProgressDao", storages = {
        @Storage(value = "idea.book.settings.dao.xml")
})
public class ReadingProgressDao implements PersistentStateComponent<ReadingProgressDao> {

    // 全局搜索类型
    public String bookName;

    // 当前章节下标
    public int nowChapterIndex;

    // 章节集合
    public List<String> chapters = new ArrayList<>();

    // 章节内容
    public String textContent;

    public String importPath;

    public ReadingProgressDao() {
    }

    public ReadingProgressDao(String bookName, int nowChapterIndex, List<String> chapters, String textContent, String importPath) {
        this.bookName = bookName;
        this.nowChapterIndex = nowChapterIndex;
        this.chapters = chapters;
        this.textContent = textContent;
        this.importPath = importPath;
    }

    public static ReadingProgressDao getInstance() {
        return ApplicationManager.getApplication().getService(ReadingProgressDao.class);
    }

    @Override
    @Nullable
    public ReadingProgressDao getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ReadingProgressDao s) {
        XmlSerializerUtil.copyBean(s, this);
    }
}

