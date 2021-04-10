package com.epam.tishkin.library;

import java.util.Objects;

public class Book {
    private final String title;
    private final String author;
    private final long ISBNumber;
    private final int year;

    public Book(String title, String author, long ISBNumber, int year) {
        this.title = title;
        this.author = author;
        this.ISBNumber = ISBNumber;
        this.year = year;
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
        return title + " " + ISBNumber + " " + year;
    }
}
