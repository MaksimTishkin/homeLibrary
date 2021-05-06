package com.epam.tishkin.dao.impl;

import com.epam.tishkin.models.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DBConnector {
    private static DBConnector connector;
    private final SessionFactory sessionFactory;

    private DBConnector() {
        Configuration configuration = new Configuration().configure();
        configuration.addAnnotatedClass(Author.class);
        configuration.addAnnotatedClass(Book.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Role.class);
        configuration.addAnnotatedClass(Bookmark.class);
        sessionFactory = configuration.buildSessionFactory();
    }

    public static DBConnector getConnector() {
        if (connector == null) {
            connector = new DBConnector();
        }
        return connector;
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
