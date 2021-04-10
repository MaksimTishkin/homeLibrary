package com.epam.tishkin.library;

import java.util.*;

public class Library {
    private List<Author> authors;
    Comparator<Author> authorComparator = Comparator.comparing(Author::getName);
    Iterator<Author> authorIterator;
    Iterator<Book> bookIterator;

    public List<Author> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        return authors;
    }

    public boolean addBook(String title, String author, long ISBNumber, int year, int pagesNumber) {
        Book book = new Book(title, author, ISBNumber, year, pagesNumber);
        Author currentAuthor = new Author(author);
        int index = Collections.binarySearch(authors, currentAuthor, authorComparator);
        if (index >= 0) {
            currentAuthor = authors.get(index);
            Optional<Book> currentBook = currentAuthor.getBooks()
                    .stream()
                    .filter(book::equals)
                    .findFirst();
            if (currentBook.isPresent()) {
                return false;
            }
            currentAuthor.getBooks().add(book);
        } else {
            currentAuthor.getBooks().add(book);
            authors.add(currentAuthor);
            authors.sort(authorComparator);
        }
        return true;
    }

    public boolean deleteBook (String title, String author) {
        Author currentAuthor = new Author(author);
        int index = Collections.binarySearch(authors, currentAuthor, authorComparator);
        if (index >= 0) {
            currentAuthor = authors.get(index);
            Optional<Book> currentBook = currentAuthor.getBooks()
                    .stream()
                    .filter(x -> title.equals(x.getTitle()))
                    .findFirst();
            if (currentBook.isPresent()) {
                currentAuthor.getBooks().remove(currentBook.get());
                return true;
            }
        }
        return false;
    }

    public boolean addAuthor(String name) {
        Author newAuthor = new Author(name);
        int index = Collections.binarySearch(authors, newAuthor, authorComparator);
        if (index < 0) {
            authors.add(newAuthor);
            authors.sort(authorComparator);
            return true;
        }
        return false;
    }

    public boolean deleteAuthor(String name) {
        int index = Collections.binarySearch(authors, new Author(name),authorComparator);
        if (index >= 0) {
            authors.remove(index);
            authors.sort(authorComparator);
            return true;
        }
        return false;
    }
}
