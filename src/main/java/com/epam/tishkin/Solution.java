package com.epam.tishkin;
/**
import com.epam.tishkin.authorization.handler.Handler;
import com.epam.tishkin.authorization.handler.LoginHandler;
import com.epam.tishkin.exception.InvalidAutorizationException;
import com.epam.tishkin.client.Visitor;
*/
import com.epam.tishkin.models.Author;
import com.epam.tishkin.models.Book;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Solution {
    final static Logger logger = LogManager.getLogger(Solution.class);
    static Configuration configuration;
    static SessionFactory sessionFactory;

    public static void main(String[] args) {
        /**
         String login;
         String password;
         try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
         System.out.println("Enter your login");
         login = consoleReader.readLine();
         System.out.println("Enter your password");
         password = consoleReader.readLine();
         Visitor visitor = authorization(login, password);
         LibraryAPI libraryAPI = new LibraryAPI(visitor);
         libraryAPI.startLibraryUse();
         } catch (InvalidAutorizationException | IOException e) {
         logger.error(e.getMessage());
         }
         */
        configuration = new Configuration().configure();
        configuration.addAnnotatedClass(Author.class);
        configuration.addAnnotatedClass(Book.class);
        sessionFactory = configuration.buildSessionFactory();
        Solution solution = new Solution();
        String authorName = "Lermontov";
        Book book = new Book("Borodino", "1336985478569", 1837, 25);
        solution.addBook(book, authorName);
        authorName = "Lermontov";
        book = new Book("Mtsyri", "5478966652325", 1839, 125);
        solution.addBook(book, authorName);
        authorName = "Pushkin";
        book = new Book("Ruslan and Lyudmila", "1458963258796", 1820, 135);
        solution.addBook(book, authorName);
        sessionFactory.close();
    }

    private void addBook(Book book, String authorName) {

    }

    private void getBooksByAuthor(String authorName) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Author author = session.get(Author.class, authorName);
        if (author != null) {
            author.getBooks().forEach(System.out::println);
        }
        transaction.commit();
        session.close();
    }
}
/**
    private static Visitor authorization(String login, String password) throws InvalidAutorizationException {
            Handler handler = new LoginHandler(login, password);
            return handler.check();
    }
 */

