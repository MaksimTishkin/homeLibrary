package com.epam.tishkin.dao;

import com.epam.tishkin.exception.InvalidAutorizationException;
import com.epam.tishkin.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

public class UserDatabaseDAO implements UserDAO {
    final static Logger logger = LogManager.getLogger(UserDatabaseDAO.class);
    private DBConnector connector;

    public UserDatabaseDAO() {
        connector = new DBConnector();
    }

    public User getUser(String login, String password) throws InvalidAutorizationException {
        try (Session session = connector.openSession()) {
            User user = session.get(User.class, login);
            if (user != null) {
                if (user.getPassword().equals(password)) {
                    return user;
                }
            }
        }
        throw new InvalidAutorizationException("Incorrect login/password");
    }
}
