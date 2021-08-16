package com.epam.tishkin.server.dao;

import com.epam.tishkin.models.Book;
import com.epam.tishkin.models.Bookmark;
import com.epam.tishkin.models.User;

import java.io.File;
import java.util.List;

public interface LibraryDAO {
    User userAuthorization(String login, String password);
    boolean addUser(User user);
    boolean blockUser(String login);
    boolean addBookmark(Bookmark newBookmark, String login);
    boolean deleteBookmark(String bookTitle, String login);
    List<Bookmark> getBookmarks(String userLogin);
    boolean addBook(Book book);
    boolean deleteBook(String authorName, String title);
    boolean addAuthor(String authorName);
    boolean deleteAuthor(String authorName);
    int addBooksFromCatalog(File file);
    List<Book> getBooksByTitle(String title);
    List<Book> getBooksByAuthor(String authorName);
    Book getBookByISBN(String ISBNumber);
    List<Book> getBooksByYearRange(int startYear, int finishYear);
    List<Book> getBooksByYearPagesNumberAndTitle(int year, int pages, String title);
    Book getBookByFullTitle(String title);
}
