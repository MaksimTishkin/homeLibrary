package com.epam.tishkin.library;

import com.epam.tishkin.authorization.exception.AuthorDoesNotExistException;
import com.epam.tishkin.authorization.exception.BookDoesNotExistException;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Library {
    private List<Author> authors;
    final static Logger logger = LogManager.getLogger(Library.class);

    public List<Author> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        return authors;
    }

    public boolean addBook(Book book) {
        Author currentAuthor = new Author(book.getAuthor());
        int index = Collections.binarySearch(authors, currentAuthor, Comparator.comparing(Author::getName));
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
            authors.sort(Comparator.comparing(Author::getName));
        }
        return true;
    }

    public boolean deleteBook (String title, String author) {
        Author currentAuthor = new Author(author);
        int index = Collections.binarySearch(authors, currentAuthor, Comparator.comparing(Author::getName));
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
        int index = Collections.binarySearch(authors, newAuthor, Comparator.comparing(Author::getName));
        if (index < 0) {
            authors.add(newAuthor);
            authors.sort(Comparator.comparing(Author::getName));
            return true;
        }
        return false;
    }

    public boolean deleteAuthor(String name) {
        int index = Collections.binarySearch(authors, new Author(name), Comparator.comparing(Author::getName));
        if (index >= 0) {
            authors.remove(index);
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
                Book book = new Book(bookParameters[0], bookParameters[1], ISBNumber, year, pagesNumber);
                if (addBook(book)) {
                    count++;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return count;
    }

    public long addBooksFromJSON(String fileName) {
        long count = 0;
        try (FileReader reader = new FileReader(fileName)) {
            Gson gson = new Gson();
            Author catalog = gson.fromJson(reader, Author.class);
            count = catalog.getBooks()
                    .stream()
                    .filter(this::addBook)
                    .count();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return count;
    }

    public Book searchBookForTitle(String title) throws BookDoesNotExistException {
        for (Author author : authors) {
            Optional<Book> foundBook = author.getBooks()
                    .stream()
                    .filter(b -> b.getTitle().contains(title))
                    .findFirst();
            if (foundBook.isPresent()) {
                return foundBook.get();
            }
        }
        throw new BookDoesNotExistException("Book not found");
    }

    public void addBookmark(String title, int page) throws BookDoesNotExistException {
        Book book = searchBookForTitle(title);
        book.getBookmark().setMark(true);
        book.getBookmark().setPage(page);
    }

    public void deleteBookmark(String title) throws BookDoesNotExistException {
        Book book = searchBookForTitle(title);
        book.getBookmark().setMark(false);
    }

    public Author searchBooksForAuthor(String author) throws AuthorDoesNotExistException {
        Optional <Author> foundAuthor = authors
                .stream()
                .filter(a -> a.getName().contains(author))
                .findFirst();
        return foundAuthor.orElseThrow(() -> new AuthorDoesNotExistException("Author not found"));
    }

    public Book searchBookForISBN(long ISBNumber) throws BookDoesNotExistException {
        for (Author author : authors) {
            Optional<Book> foundBook = author.getBooks()
                    .stream()
                    .filter(b -> ISBNumber == b.getISBNumber())
                    .findFirst();
            if (foundBook.isPresent()) {
                return foundBook.get();
            }
        }
        throw new BookDoesNotExistException("Book not found");
    }

    public List<Book> searchBooksByYearRange(int startYear, int finishYear) {
        List<Book> books = new ArrayList<>();
        authors.forEach(author -> {
            for (Book currentBook : author.getBooks()) {
                if (currentBook.getYear() >= startYear && currentBook.getYear() <= finishYear) {
                    books.add(currentBook);
                }
            }
        });
        return books;
    }

    public Book searchBookByYearPagesNumberAndTitle(int year, int pages, String title) throws BookDoesNotExistException {
        for (Author author : authors) {
            Optional<Book> book = author.getBooks()
                    .stream()
                    .filter(b -> b.getYear() == year)
                    .filter((b -> b.getPagesNumber() == pages))
                    .filter(b -> b.getTitle().contains(title))
                    .findFirst();
            if (book.isPresent()) {
                return book.get();
            }
        }
        throw new BookDoesNotExistException("Book not found");
    }

    public List<Book> searchBooksWithBookmark() {
        List<Book> books = new ArrayList<>();
        authors.forEach(author -> {
            for (Book currentBook : author.getBooks()) {
                if (currentBook.getBookmark().getMark()) {
                    books.add(currentBook);
                }
            }
        });
        return books;
    }
}
