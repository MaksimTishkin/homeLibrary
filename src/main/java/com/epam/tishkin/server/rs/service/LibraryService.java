package com.epam.tishkin.server.rs.service;

import com.epam.tishkin.models.Book;
import com.epam.tishkin.models.Bookmark;
import com.epam.tishkin.models.User;
import org.jvnet.hk2.annotations.Contract;

import java.io.File;
import java.util.List;

@Contract
public interface LibraryService {
    User authorization(String login, String password);
    boolean addNewBook(Book book);
    boolean deleteBook(String authorName, String bookTitle);
    boolean addAuthor(String authorName);
    boolean deleteAuthor(String authorName);
    int addBooksFromCatalog(File file);
    List<Book> searchBookForTitle(String title);
    List<Book> searchBooksForAuthor(String authorName);
    Book searchBookForISBN(String isbn);
    List<Book> searchBooksByYearRange(int initialYear, int finalYear);
    List<Book> searchBookByYearPagesNumberAndTitle(int year, int pages, String title);
    Book findBookByFullTitle(String bookTitle);
    boolean addBookmark(Bookmark bookmark, String login);
    boolean deleteBookmark(String bookTitle, String login);
    List<Bookmark> showBooksWithBookmarks(String login);
    boolean addNewUser(User user);
    boolean deleteUser(String login);
    List<String> showHistory();
}
