package com.epam.tishkin.ws.impl;

import com.epam.tishkin.models.Book;
import com.epam.tishkin.models.User;
import com.epam.tishkin.ws.Library;
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
import org.hibernate.query.Query;

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
                    return user;
                }
            }
        }
        return null;
    }

    private String getLogin() {
        MessageContext messageContext = webServiceContext.getMessageContext();
        Map<?, ?> http_headers = (Map<?, ?>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        List<?> userList = (List<?>) http_headers.get("Username");
        return userList.get(0).toString();
    }

    public boolean addUser(String login, String password) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            User visitor = session.get(User.class, login);
            if (visitor != null) {
                logger.info("This user already exists - " + visitor.getLogin());
                return false;
            }
            visitor = new User(login, password, Role.VISITOR);
            session.save(visitor);
            transaction.commit();
            HistoryWriter.write(getLogin(), "New user added - " + login);
            return true;
        }
    }

    public boolean blockUser(String login) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            User visitor = session.get(User.class, login);
            if (visitor == null) {
                logger.info("User does not exist - " + login);
                return false;
            }
            session.delete(visitor);
            transaction.commit();
            logger.info("User deleted - " + visitor.getLogin());
            return true;
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

    public boolean addBookmark(String bookTitle, int pageNumber, String currentLogin) {
        Bookmark currentBookmark = new Bookmark(bookTitle, pageNumber, currentLogin);
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            Query<Bookmark> query = session.createQuery("FROM Bookmark WHERE User_login =: login", Bookmark.class);
            query.setParameter("login", currentLogin);
            List<Bookmark> foundBookmarks = query.getResultList();
            Optional<Bookmark> bookmark = foundBookmarks
                    .stream()
                    .filter(b -> bookTitle.equals(b.getTitle()))
                    .findFirst();
            if (bookmark.isPresent()) {
                logger.info("The bookmark already exists in this book");
                return false;
            }
            session.save(currentBookmark);
            transaction.commit();
            logger.info("Bookmark was added to the " + bookTitle + " book on the page " + pageNumber);
            return true;
        }
    }

    public boolean deleteBookmark(String bookTitle, String currentLogin) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            Query<Bookmark> query = session.createQuery("FROM Bookmark WHERE User_login =: login", Bookmark.class);
            query.setParameter("login", currentLogin);
            List<Bookmark> foundBookmarks = query.getResultList();
            Optional<Bookmark> bookmark = foundBookmarks
                    .stream()
                    .filter(b -> bookTitle.equals(b.getTitle()))
                    .findFirst();
            if (bookmark.isPresent()) {
                session.delete(bookmark.get());
                transaction.commit();
                logger.info("Bookmark deleted - book title: " + bookTitle);
                return true;
            }
            logger.info("There is no bookmark in this book: " + bookTitle);
            return false;
        }
    }

    public List<Bookmark> showBooksWithBookmarks(String currentLogin) {
        try (Session session = HibernateUtil.getSession()) {
            Query<Bookmark> query = session.createQuery("FROM Bookmark WHERE User_login =: login", Bookmark.class);
            query.setParameter("login", currentLogin);
            return query.getResultList();
        }
    }
}
