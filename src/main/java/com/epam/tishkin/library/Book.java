package com.epam.tishkin.library;

public class Book {
    private String name;
    private String author;
    private long ISBNNumber;
    private int year;

    public Book(String name, String author, long ISBNNumber, int year) {
        this.name = name;
        this.author = author;
        this.ISBNNumber = ISBNNumber;
        this.year = year;
    }

    public String getName() {
        return name;
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
        return name + " " + ISBNNumber + " " + year;
    }
}
