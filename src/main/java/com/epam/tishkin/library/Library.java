package com.epam.tishkin.library;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Author> authors;

    public List<Author> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        return authors;
    }

    public void addBook(Book book) {
        for (Author author : authors) {
            if (book.getAuthor().equals(author.getName())) {
                author.getBooks().add(book);
                return;
            }
        }
        Author author = new Author(book.getAuthor());
        author.getBooks().add(book);
        authors.add(author);
    }
}
