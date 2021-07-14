package com.epam.tishkin.server.dao.impl;

import com.epam.tishkin.server.dao.HibernateUtil;
import com.epam.tishkin.server.dao.HistoryManager;
import com.epam.tishkin.server.dao.LibraryDAO;
import com.epam.tishkin.models.*;
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
import java.util.List;
import java.util.Optional;

public class LibraryDAOImpl implements LibraryDAO {
    final static Logger logger = LogManager.getLogger(LibraryDAOImpl.class);

    public User userAuthorization(String login, String password) {
        try (Session session = HibernateUtil.getSession()) {
            User user = session.get(User.class, login);
            if (user != null) {
                if (user.getPassword().equals(password)) {
                    HistoryManager.write(login, " is connected");
                    return user;
                }
            }
        }
        return null;
    }

    public boolean addUser(User user) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            User visitor = session.get(User.class, user.getLogin());
            if (visitor != null) {
                return false;
            }
            session.save(user);
            transaction.commit();
            return true;
        }
    }

    public boolean blockUser(String login) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            User visitor = session.get(User.class, login);
            if (visitor == null) {
                return false;
            }
            session.delete(visitor);
            transaction.commit();
            return true;
        }
    }

    public boolean addBookmark(Bookmark newBookmark, String login) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            User user = session.get(User.class, login);
            Optional<Bookmark> bookmark = user.getBookmarks()
                    .stream()
                    .filter(b -> newBookmark.getTitle().equals(b.getTitle()))
                    .findFirst();
            if (bookmark.isPresent()) {
                return false;
            }
            newBookmark.setUser(user);
            session.save(newBookmark);
            transaction.commit();
            return true;
        }
    }

    public boolean deleteBookmark(String bookTitle, String userLogin) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            Query<Bookmark> query = session.createQuery("FROM Bookmark WHERE User_login =: login and Book_title =: title", Bookmark.class);
            query.setParameter("login", userLogin);
            query.setParameter("title", bookTitle);
            List<Bookmark> foundBookmarks = query.getResultList();
            if (!foundBookmarks.isEmpty()) {
                session.delete(foundBookmarks.get(0));
                transaction.commit();
                return true;
            }
            return false;
        }
    }

    public List<Bookmark> showBooksWithBookmarks(String userLogin) {
        try (Session session = HibernateUtil.getSession()) {
            Query<Bookmark> query = session.createQuery("FROM Bookmark WHERE User_login =: login", Bookmark.class);
            query.setParameter("login", userLogin);
            return query.getResultList();
        }
    }

    public boolean addBook(Book book) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            Author author = session.get(Author.class, book.getAuthor().getName());
            if (author == null) {
                session.save(book.getAuthor());
            } else {
                Optional<Book> currentBook = author.getBook()
                        .stream()
                        .filter(b -> book.getTitle().equals(b.getTitle()))
                        .findFirst();
                if (currentBook.isPresent()) {
                    return false;
                }
            }
            session.save(book);
            transaction.commit();
            return true;
        }
    }

    public boolean deleteBook(String authorName, String title) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            Query<Book> query = session.createQuery("FROM Book WHERE Author_Name =: author and Title =: bookTitle", Book.class);
            query.setParameter("author", authorName);
            query.setParameter("bookTitle", title);
            List<Book> foundBooks = query.getResultList();
            if (foundBooks.isEmpty()) {
                return false;
            }
            Book book = foundBooks.get(0);
            session.delete(book);
            transaction.commit();
            return true;
        }
    }

    public boolean addAuthor(String authorName) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            Author author = session.get(Author.class, authorName);
            if (author != null) {
                return false;
            }
            author = new Author(authorName);
            session.save(author);
            transaction.commit();
            return true;
        }
    }

    public boolean deleteAuthor(String authorName) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            Author author = session.get(Author.class, authorName);
            if (author == null) {
                return false;
            }
            session.delete(author);
            transaction.commit();
            return true;
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
                Book book = new Book(title, ISBNumber, year, pagesNumber, new Author(author));
                if (addBook(book)) {
                    count++;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return count;
    }

    private int addBooksFromJSON(File file) {
        final int[] count = {0};
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            BooksList list = gson.fromJson(reader, BooksList.class);
            list.getBooks().forEach(b -> {
                if (addBook(b)) {
                    count[0]++;
                }
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return count[0];
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
            List<Book> foundBook = query.getResultList();
            if (!foundBook.isEmpty()) {
                book = foundBook.get(0);
            }
        }
        return book;
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

    public List<Book> searchBooksByYearRange(int startYear, int finishYear) {
        try (Session session = HibernateUtil.getSession()) {
            Query<Book> query = session.createQuery("FROM Book WHERE publicationYear BETWEEN :startYear and :finishYear", Book.class);
            query.setParameter("startYear", startYear);
            query.setParameter("finishYear", finishYear);
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
