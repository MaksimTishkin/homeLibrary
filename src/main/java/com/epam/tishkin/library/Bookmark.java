package com.epam.tishkin.library;

public class Bookmark {
    private boolean isMark;
    private int page;

    public boolean getMark() {
        return isMark;
    }

    public void setMark(boolean status) {
        isMark = status;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
