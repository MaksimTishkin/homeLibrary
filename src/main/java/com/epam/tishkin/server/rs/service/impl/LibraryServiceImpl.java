package com.epam.tishkin.server.rs.service.impl;

import com.epam.tishkin.models.Book;
import com.epam.tishkin.models.Role;
import com.epam.tishkin.models.User;
import com.epam.tishkin.server.dao.LibraryDAO;
import com.epam.tishkin.server.dao.impl.LibraryDAOImpl;
import com.epam.tishkin.server.rs.service.LibraryService;
import jakarta.ws.rs.core.Response;
import org.jvnet.hk2.annotations.Service;

@Service
public class LibraryServiceImpl implements LibraryService {
    LibraryDAO libraryDAO = new LibraryDAOImpl();

    @Override
    public boolean addNewBook(Book book) {
        return libraryDAO.addBook(book);
    }

    @Override
    public User authorization(String login, String password) {
        return libraryDAO.userAuthorization(login, password);
    }
}
