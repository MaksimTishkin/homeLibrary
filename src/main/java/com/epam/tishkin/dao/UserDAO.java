package com.epam.tishkin.dao;

import com.epam.tishkin.exception.InvalidAutorizationException;
import com.epam.tishkin.models.User;

public interface UserDAO {

    User userAuthorization(String login, String password) throws InvalidAutorizationException;
    void addUser(String login, String password);
    void blockUser(String login);
    void showHistory();
    void addBookmark(String bookTitle, int pageNumber);
    boolean deleteBookmark(String bookTitle);
    void showBooksWithBookmarks();
    void closeConnection();
    User getUser();
    void setUser(User user);
}
