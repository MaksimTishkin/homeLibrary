package com.epam.tishkin.models;

import java.util.ArrayList;
import java.util.List;

public class BooksList {
    private List<Book> books;

    public List<Book> getBooks() {
        if (books == null) {
            books = new ArrayList<>();
        }
        return books;
    }
}
