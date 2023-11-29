package com.oeong.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.oeong.vo.BookData;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@State(name = "BookService", storages = {
        @Storage(value = "idea.book.settings.dao.xml")
})
public class BookService implements PersistentStateComponent<BookService.State> {

    private State myBookState = new State();

    public static BookService getInstance() {
        return ApplicationManager.getApplication().getService(BookService.class);
    }

    @Override
    public State getState() {
        return myBookState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myBookState = state;
    }

    public HashMap<String, BookData> getBookData() {
        return myBookState.getBookData();
    }

    @Data
    static class State {
        private HashMap<String, BookData> bookData = new HashMap<>();
    }

}
