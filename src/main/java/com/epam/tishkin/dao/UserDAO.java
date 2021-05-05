package com.epam.tishkin.dao;

import com.epam.tishkin.models.User;

public interface UserDAO {
    boolean userAuthorization(String login, String password);
    User getUser();
    void addUser(String login, String password);
    void blockUser(String login);
    void showHistory();
    void addBookmark(String bookTitle, int pageNumber);
    boolean deleteBookmark(String bookTitle);
    void showBooksWithBookmarks();
    void closeConnection();
}
