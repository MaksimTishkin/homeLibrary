package com.epam.tishkin.models;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class BookmarksList {
    @XmlElement
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
