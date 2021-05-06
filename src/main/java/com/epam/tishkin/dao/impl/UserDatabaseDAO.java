package com.epam.tishkin.dao.impl;

import com.epam.tishkin.dao.UserDAO;
import com.epam.tishkin.models.Bookmark;
import com.epam.tishkin.models.Role;
import com.epam.tishkin.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class UserDatabaseDAO implements UserDAO {
    private static final Properties properties = new Properties();
    final static Logger logger = LogManager.getLogger(UserDatabaseDAO.class);
    private final DBConnector connector;
    private User user;

    public UserDatabaseDAO() {
        connector = DBConnector.getConnector();
    }

    public boolean userAuthorization(String login, String password) {
        try (Session session = connector.openSession()) {
            user = session.get(User.class, login);
            if (user != null) {
                if (user.getPassword().equals(password)) {
                    logger.info(login + " is connected");
                    return true;
                }
            }
        }
        logger.info("Incorrect login/password");
        return false;
    }

    public void addUser(String login, String password) {
        try (Session session = connector.openSession()) {
            Transaction transaction = session.beginTransaction();
            User visitor = session.get(User.class, login);
            if (visitor == null) {
                visitor = new User(login, password, Role.VISITOR);
                session.save(visitor);
                transaction.commit();
                logger.info("New user added - " + visitor.getLogin());
            } else {
                logger.info("This user already exists - " + visitor.getLogin());
            }
        }
    }

    public void blockUser(String login) {
        try (Session session = connector.openSession()) {
            Transaction transaction = session.beginTransaction();
            User visitor = session.get(User.class, login);
            if (visitor != null) {
                session.delete(visitor);
                transaction.commit();
                logger.info("User deleted - " + visitor.getLogin());
            } else {
                logger.info("User does not exist - " + login);
            }
        }
    }

    public void showHistory() {
        try (FileReader readerForProperties = new FileReader("src/main/resources/config.properties")) {
            properties.load(readerForProperties);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(properties.getProperty("pathFromHistory")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void addBookmark(String bookTitle, int pageNumber) {
        try (Session session = connector.openSession()) {
            Transaction transaction = session.beginTransaction();
            Bookmark bookmark = new Bookmark(bookTitle, pageNumber);
            user = session.get(User.class, user.getLogin());
            user.addBookmark(bookmark);
            session.save(user);
            transaction.commit();
            logger.info("Bookmark added - book title: " + bookTitle + " page: " + pageNumber);
        }
    }

    public boolean deleteBookmark(String bookTitle) {
        try (Session session = connector.openSession()) {
            Transaction transaction = session.beginTransaction();
            user = session.get(User.class, user.getLogin());
            Optional<Bookmark> bookmark = user.getBookmarks()
                    .stream()
                    .filter(b -> bookTitle.equals(b.getTitle()))
                    .findFirst();
            if (bookmark.isPresent()) {
                user.removeBookmark(bookmark.get());
                session.save(user);
                transaction.commit();
                logger.info("Bookmark deleted - book title: " + bookTitle);
                return true;
            } else {
                logger.info("There is no bookmark in this book: " + bookTitle);
            }
            return false;
        }
    }

    public void showBooksWithBookmarks() {
        try (Session session = connector.openSession()) {
            user = session.get(User.class, user.getLogin());
            List<Bookmark> bookmarks = user.getBookmarks();
            if (!bookmarks.isEmpty()) {
                bookmarks.forEach(b -> logger.info("Book with bookmark - " + b.getTitle()
                        + " on page " + b.getPage()));
            } else {
                logger.info("No books with bookmarks");
            }
        }
    }

    public User getUser() {
        return user;
    }

    public void closeConnection() {
        connector.closeSessionFactory();
    }
}
