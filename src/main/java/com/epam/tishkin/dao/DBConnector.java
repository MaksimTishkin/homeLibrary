package com.epam.tishkin.dao;

import com.epam.tishkin.models.Author;
import com.epam.tishkin.models.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DBConnector {
    private Configuration configuration;
    private SessionFactory sessionFactory;

    public DBConnector() {
        configuration = new Configuration().configure();
        configuration.addAnnotatedClass(Author.class);
        configuration.addAnnotatedClass(Book.class);
        sessionFactory = configuration.buildSessionFactory();
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }

    public void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
