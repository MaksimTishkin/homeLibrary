package com.epam.tishkin.library;

public class Book {
    private String title;
    private String author;
    private long ISBNNumber;
    private int year;

    public Book(String title, String author, long ISBNNumber, int year) {
        this.title = title;
        this.author = author;
        this.ISBNNumber = ISBNNumber;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public  long getISBNNumber() {
        return ISBNNumber;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return title + " " + ISBNNumber + " " + year;
    }
}
