package com.epam.tishkin.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Visitor {
    private final String name;
    private final List<Bookmark> myBookmarks;

    Visitor(String name) {
        this.name = name;
        myBookmarks = new ArrayList<>();
    }

    public void addBookmark(String title, int page) {
        Bookmark bookmark = new Bookmark(title, page);
        myBookmarks.add(bookmark);
    }

    public boolean deleteBookmark(String title) {
        Optional<Bookmark> currentBookmark = myBookmarks
                .stream()
                .filter(b -> b.getTitle().equals(title))
                .findFirst();
        currentBookmark.ifPresent(myBookmarks::remove);
        return currentBookmark.isPresent();
    }

    public String getName() {
        return name;
    }

    public List<Bookmark> getMyBookmarks() {
        return myBookmarks;
    }
}