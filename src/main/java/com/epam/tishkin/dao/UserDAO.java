package com.epam.tishkin.dao;

import com.epam.tishkin.exception.InvalidAutorizationException;
import com.epam.tishkin.models.User;

public interface UserDAO {

    User getUser(String login, String password) throws InvalidAutorizationException;
}
