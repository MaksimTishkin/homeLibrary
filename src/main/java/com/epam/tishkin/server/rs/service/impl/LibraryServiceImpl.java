package com.epam.tishkin.server.rs.service.impl;

import com.epam.tishkin.models.Book;
import com.epam.tishkin.models.Bookmark;
import com.epam.tishkin.models.User;
import com.epam.tishkin.server.dao.HistoryManager;
import com.epam.tishkin.server.dao.LibraryDAO;
import com.epam.tishkin.server.dao.impl.LibraryDAOImpl;
import com.epam.tishkin.server.rs.service.LibraryService;
import org.jvnet.hk2.annotations.Service;

import java.io.File;
import java.util.List;

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

    @Override
    public boolean deleteBook(String authorName, String bookTitle) {
        return libraryDAO.deleteBook(authorName, bookTitle);
    }

    @Override
    public boolean addAuthor(String authorName) {
        return libraryDAO.addAuthor(authorName);
    }

    @Override
    public boolean deleteAuthor(String authorName) {
        return libraryDAO.deleteAuthor(authorName);
    }

    @Override
    public int addBooksFromCatalog(File file) {
        return libraryDAO.addBooksFromCatalog(file);
    }

    @Override
    public List<Book> searchBookForTitle(String title) {
        return libraryDAO.searchBookForTitle(title);
    }

    @Override
    public List<Book> searchBooksForAuthor(String authorName) {
        return libraryDAO.searchBooksForAuthor(authorName);
    }

    @Override
    public Book searchBookForISBN(String isbn) {
        return libraryDAO.searchBookForISBN(isbn);
    }

    @Override
    public List<Book> searchBooksByYearRange(int startYear, int finishYear) {
        return libraryDAO.searchBooksByYearRange(startYear, finishYear);
    }

    @Override
    public List<Book> searchBookByYearPagesNumberAndTitle(int year, int pages, String title) {
        return libraryDAO.searchBookByYearPagesNumberAndTitle(year, pages, title);
    }

    @Override
    public Book findBookByFullTitle(String bookTitle) {
        return libraryDAO.findBookByFullTitle(bookTitle);
    }

    @Override
    public boolean addBookmark(Bookmark bookmark, String login) {
        return libraryDAO.addBookmark(bookmark, login);
    }

    @Override
    public boolean deleteBookmark(String bookTitle, String login) {
        return libraryDAO.deleteBookmark(bookTitle, login);
    }

    @Override
    public List<Bookmark> showBooksWithBookmarks(String login) {
        return  libraryDAO.showBooksWithBookmarks(login);
    }

    @Override
    public boolean addNewUser(User user) {
        return libraryDAO.addUser(user);
    }

    @Override
    public boolean deleteUser(String login) {
        return libraryDAO.blockUser(login);
    }

    @Override
    public List<String> showHistory() {
        return HistoryManager.read();
    }
}
