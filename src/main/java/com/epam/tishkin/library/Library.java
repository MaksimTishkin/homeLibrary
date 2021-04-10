package com.epam.tishkin.library;

import com.epam.tishkin.authorization.AccountsList;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public int addBooksFromCSV(String fileName) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookParameters = line.split(";");
                long ISBNumber = Long.parseLong(bookParameters[2]);
                int year = Integer.parseInt(bookParameters[3]);
                int pagesNumber = Integer.parseInt(bookParameters[4]);
                if (addBook(bookParameters[0], bookParameters[1], ISBNumber, year, pagesNumber)) {
                    count++;
                }
            }
            if (count > 0) {
                authors.sort(authorComparator);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void addBooksFromJSON(String fileName) {
        try (FileReader reader = new FileReader(fileName)) {
            Gson gson = new Gson();
            Author catalog = gson.fromJson(reader, Author.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
