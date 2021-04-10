package com.epam.tishkin.library;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Library {
    private List<Author> authors;
    Iterator<Author> authorIterator;
    Iterator<Book> bookIterator;

    public List<Author> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        return authors;
    }

    public boolean addBook(String title, String author, long ISBNumber, int year) {
        Book book = new Book(title, author, ISBNumber, year);
        authorIterator = authors.iterator();
        while (authorIterator.hasNext()) {
            Author currentAuthor = authorIterator.next();
            if (book.getAuthor().equals(currentAuthor.getName())) {
                bookIterator = currentAuthor.getBooks().iterator();
                while (bookIterator.hasNext()) {
                    Book currentBook = bookIterator.next();
                    if (book.equals(currentBook)) {
                        return false;
                    }
                }
                currentAuthor.getBooks().add(book);
                return true;
            }
        }
        Author newAuthor = new Author(book.getAuthor());
        newAuthor.getBooks().add(book);
        authors.add(newAuthor);
        return true;
    }

    public boolean deleteBook (String title, String author) {
        authorIterator = authors.iterator();
        while(authorIterator.hasNext()) {
            Author currentAuthor = authorIterator.next();
            if (author.equals(currentAuthor.getName())) {
                bookIterator = currentAuthor.getBooks().iterator();
                while (bookIterator.hasNext()) {
                    Book currentBook = bookIterator.next();
                    if (title.equals(currentBook.getTitle())) {
                        bookIterator.remove();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean addAuthor(String name) {
        authorIterator = authors.iterator();
        while (authorIterator.hasNext()) {
            Author currentAuthor = authorIterator.next();
            if (name.equals(currentAuthor.getName())) {
                return false;
            }
        }
        Author author = new Author(name);
        authors.add(author);
        return true;
    }

    public boolean deleteAuthor(String name) {
        authorIterator = authors.iterator();
        while (authorIterator.hasNext()) {
            Author currentAuthor = authorIterator.next();
            if (name.equals(currentAuthor.getName())) {
                authorIterator.remove();
                return true;
            }
        }
        return false;
    }
}
