package com.epam.tishkin.server.rs.service;

import com.epam.tishkin.models.Book;
import com.epam.tishkin.models.Role;
import com.epam.tishkin.models.User;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface LibraryService {
    boolean addNewBook(Book book);
    User authorization(String login, String password);
}
