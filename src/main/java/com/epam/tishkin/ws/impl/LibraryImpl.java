package com.epam.tishkin.ws.impl;

import com.epam.tishkin.ws.Library;
import com.epam.tishkin.models.Author;
import com.epam.tishkin.models.AuthorsList;
import com.epam.tishkin.models.Book;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LibraryImpl implements Library {
    final static Logger logger = LogManager.getLogger(LibraryImpl.class);

    public boolean addBook(Book book, String authorName) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            Author author = session.get(Author.class, authorName);
            if (author == null) {
                author = new Author(authorName);
            }
            Optional<Book> currentBook = author.getBooks()
                    .stream()
                    .filter(b -> book.getTitle().equals(b.getTitle()))
                    .findFirst();
            if (currentBook.isPresent()) {
                logger.info(book.getTitle() + ": this book is already in the database");
                return false;
            } else {
                author.addBook(book);
                session.save(author);
                transaction.commit();
                logger.info(book.getTitle() + ": book added");
                return true;
            }
        }
    }

    public boolean deleteBook(String title, String authorName) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            Author author = session.get(Author.class, authorName);
            if (author != null) {
                Optional<Book> currentBook = author.getBooks()
                        .stream()
                        .filter(b -> b.getTitle().equals(title))
                        .findFirst();
                if (currentBook.isPresent()) {
                    Book book = currentBook.get();
                    author.removeBook(book);
                    session.save(author);
                    transaction.commit();
                    logger.info(title + ": book deleted");
                    return true;
                }
            }
            logger.info(title + ": book not found");
            return false;
        }
    }

    public boolean addAuthor(String authorName) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            Author author = session.get(Author.class, authorName);
            if (author != null) {
                logger.info(authorName + ": this author is already in the database");
                return true;

            }
            author = new Author(authorName);
            session.save(author);
            transaction.commit();
            logger.info(authorName + ": author added");
            return false;
        }
    }

    public boolean deleteAuthor(String authorName) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            Author author = session.get(Author.class, authorName);
            if (author != null) {
                session.delete(author);
                transaction.commit();
                logger.info(authorName + ": author removed");
                return true;
            }
            logger.info(authorName + ": book not found");
            return false;
        }
    }

    public int addBooksFromCatalog(File file) {
        int index = file.getName().lastIndexOf('.');
        if ("csv".equals(file.getName().substring(index + 1))) {
            return addBooksFromCSV(file);
        } else {
            return addBooksFromJSON(file);
        }
    }

    private int addBooksFromCSV(File file) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookParameters = line.split(";");
                String title = bookParameters[0];
                String author = bookParameters[1];
                String ISBNumber = bookParameters[2];
                int year = Integer.parseInt(bookParameters[3]);
                int pagesNumber = Integer.parseInt(bookParameters[4]);
                Book book = new Book(title, ISBNumber, year, pagesNumber);
                if (addBook(book, author)) {
                    count++;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return count;
    }

    private int addBooksFromJSON(File file) {
        int count = 0;
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            AuthorsList list = gson.fromJson(reader, AuthorsList.class);
            for (Author author : list.getAuthors()) {
                for (Book currentBook : author.getBooks()) {
                    if (addBook(currentBook, author.getName())) {
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return count;
    }

    public List<Book> searchBookForTitle(String title) {
        try (Session session = HibernateUtil.getSession()) {
            Query<Book> query = session.createQuery("FROM Book WHERE Title LIKE :name", Book.class);
            query.setParameter("name", "%" + title + "%");
            return query.getResultList();
        }
    }

    public List<Book> searchBooksForAuthor(String authorName) {
        try (Session session = HibernateUtil.getSession()) {
            Query<Book> query = session.createQuery("FROM Book WHERE Author_Name LIKE :name", Book.class);
            query.setParameter("name", "%" + authorName + "%");
            return query.getResultList();
        }
    }

    public Book searchBookForISBN(String ISBNumber) {
        Book book = null;
        try (Session session = HibernateUtil.getSession()) {
            Query<Book> query = session.createQuery("FROM Book WHERE ISBNumber =:number", Book.class);
            query.setParameter("number", ISBNumber);
            List <Book> foundBook = query.getResultList();
            if (!foundBook.isEmpty()) {
                book = foundBook.get(0);
            }
        }
        return book;
    }

    public List<Book> searchBooksByYearRange(int startYear, int finishYear) {
        try (Session session = HibernateUtil.getSession()) {
            Query<Book> query = session.createQuery("FROM Book WHERE publicationYear BETWEEN :startYear and :finishYear", Book.class);
            query.setParameter("startYear", startYear);
            query.setParameter("finishYear", finishYear);
            return query.getResultList();
        }
    }

    public List<Book> searchBookByYearPagesNumberAndTitle(int year, int pages, String title) {
        try (Session session = HibernateUtil.getSession()) {
            Query<Book> query = session.createQuery("FROM Book WHERE publicationYear =:year " +
                    "and pagesNumber =:pages and title LIKE :title", Book.class);
            query.setParameter("year", year);
            query.setParameter("pages", pages);
            query.setParameter("title", "%" + title + "%");
            return query.getResultList();
        }
    }

    public Book findBookByFullTitle(String title) {
        Book book = null;
        try (Session session = HibernateUtil.getSession()) {
            Query<Book> query = session.createQuery("FROM Book where title =:title", Book.class);
            query.setParameter("title", title);
            List<Book> foundBook = query.getResultList();
            if (!foundBook.isEmpty()) {
                book = foundBook.get(0);
            }
        }
        return book;
    }
}