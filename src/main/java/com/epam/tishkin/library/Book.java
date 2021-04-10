package com.epam.tishkin.library;

import java.util.Objects;

public class Book {
    private final String title;
    private final String author;
    private final long ISBNumber;
    private final int year;
    private final int pagesNumber;
    private Bookmark bookmark;

    public Book(String title, String author, long ISBNumber, int year, int pagesNumber) {
        this.title = title;
        this.author = author;
        this.ISBNumber = ISBNumber;
        this.year = year;
        this.pagesNumber = pagesNumber;
        bookmark = new Bookmark();
    }

    public Bookmark getBookmark() {
        return bookmark;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public long getISBNumber() {
        return ISBNumber;
    }

    public int getYear() {
        return year;
    }

    public int getPagesNumber() {
        return pagesNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return ISBNumber == book.ISBNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ISBNumber);
    }

    @Override
    public String toString() {
        return title + " " + ISBNumber + " " + year + " " + pagesNumber;
    }
}
