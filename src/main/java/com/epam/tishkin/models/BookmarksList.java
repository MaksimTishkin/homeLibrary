package com.epam.tishkin.models;

import java.util.ArrayList;
import java.util.List;

public class BookmarksList {
    private List<Bookmark> bookmarks;

    public BookmarksList() {
        bookmarks = new ArrayList<>();
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }
}
