package com.epam.tishkin.ws.impl;

import com.epam.tishkin.models.BooksList;
import com.epam.tishkin.ws.Library;
import com.epam.tishkin.models.Author;
import com.epam.tishkin.models.Book;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
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

@WebService(endpointInterface = "com.epam.tishkin.ws.Library")
public class LibraryImpl implements Library {
    final static Logger logger = LogManager.getLogger(LibraryImpl.class);

    @Resource
    WebServiceContext webServiceContext;

    public boolean addBook(String title, String ISBNumber, int year, int pages, String bookAuthor) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            Author author = session.get(Author.class, bookAuthor);
            if (author == null) {
                author = new Author(bookAuthor);
                session.save(author);
            } else {
                Query<Book> query = session.createQuery("FROM Book WHERE Author_Name =: author", Book.class);
                query.setParameter("author", bookAuthor);
                Optional<Book> currentBook = query.getResultList()
                        .stream()
                        .filter(b -> title.equals(b.getTitle()))
                        .findFirst();
                if (currentBook.isPresent()) {
                    return false;
                }
            }
            Book book = new Book(title, ISBNumber, year, pages, bookAuthor);
            session.save(book);
            transaction.commit();
            HistoryManager.write(getLogin(), "Book added - " + title);
            return true;
        }
    }

    public boolean deleteBook(String title, String authorName) {
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
            HistoryManager.write(getLogin(), "Book removed - " + title);
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
            HistoryManager.write(getLogin(), "Author added - " + authorName);
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
            HistoryManager.write(getLogin(), "Author removed - " + authorName);
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
                if (addBook(title, ISBNumber, year, pagesNumber, author)) {
                    count++;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        HistoryManager.write(getLogin(), "Books added from CSV catalog - " + count);
        return count;
    }

    private int addBooksFromJSON(File file) {
        final int[] count = {0};
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            BooksList list = gson.fromJson(reader, BooksList.class);
            list.getBooks().forEach(b -> {
                if (addBook(b.getTitle(), b.getISBNumber(), b.getPublicationYear(),
                        b.getPagesNumber(), b.getAuthor())) {
                    count[0]++;
                }
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        HistoryManager.write(getLogin(), "Books added from JSON catalog - " + count[0]);
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

    private String getLogin() {
        MessageContext messageContext = webServiceContext.getMessageContext();
        Map<?, ?> http_headers = (Map<?, ?>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        List<?> userList = (List<?>) http_headers.get("Username");
        return userList.get(0).toString();
    }
}