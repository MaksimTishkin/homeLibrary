package com.epam.tishkin.library;

public class Bookmark {
    private final String title;
    private final int page;

    public Bookmark(String title, int page) {
        this.title = title;
        this.page = page;
    }

    public String getTitle() {
        return title;
    }

    public int getPage() {
        return page;
    }

    @Override
    public String toString() {
        return "Book title: " + title + " page with bookmark: " + page;
    }
}
