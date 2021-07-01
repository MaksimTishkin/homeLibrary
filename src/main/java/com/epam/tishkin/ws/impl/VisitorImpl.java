package com.epam.tishkin.ws.impl;

import com.epam.tishkin.models.User;
import com.epam.tishkin.ws.Visitor;
import com.epam.tishkin.models.Bookmark;
import com.epam.tishkin.models.Role;
import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@WebService(endpointInterface = "com.epam.tishkin.ws.Visitor")
public class VisitorImpl implements Visitor {
    private static final Properties properties = new Properties();
    final static Logger logger = LogManager.getLogger(VisitorImpl.class);

    @Resource
    WebServiceContext webServiceContext;

    public User userAuthorization() {
        MessageContext messageContext = webServiceContext.getMessageContext();
        Map<?, ?> http_headers = (Map<?, ?>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        List<?> userList = (List<?>) http_headers.get("Username");
        List<?> passList = (List<?>) http_headers.get("Password");
        String login = "";
        String password = "";
        if(userList != null) {
            login = userList.get(0).toString();
        }
        if(passList != null) {
            password = passList.get(0).toString();
        }
        try (Session session = HibernateUtil.getSession()) {
            User user = session.get(User.class, login);
            if (user != null) {
                if (user.getPassword().equals(password)) {
                    logger.info(login + " is connected");
                    logger.info(user);
                    return user;
                }
            }
        }
        return null;
    }

    public void addUser(String login, String password) {
        try (Session session = HibernateUtil.getSession()) {
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
        try (Session session = HibernateUtil.getSession()) {
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

    public void addBookmark(String bookTitle, int pageNumber, String login) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            Bookmark bookmark = new Bookmark(bookTitle, pageNumber);
            User user = session.get(User.class, login);
            user.addBookmark(bookmark);
            session.save(user);
            transaction.commit();
        }
    }

    public boolean deleteBookmark(String bookTitle, String login) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            User user = session.get(User.class, login);
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

    public void showBooksWithBookmarks(String login) {
        try (Session session = HibernateUtil.getSession()) {
            User user = session.get(User.class, login);
            List<Bookmark> bookmarks = user.getBookmarks();
            if (!bookmarks.isEmpty()) {
                bookmarks.forEach(b -> logger.info("Book with bookmark - " + b.getTitle()
                        + " on page " + b.getPage()));
            } else {
                logger.info("No books with bookmarks");
            }
        }
    }
}
