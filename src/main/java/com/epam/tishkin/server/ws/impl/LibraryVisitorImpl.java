package com.epam.tishkin.server.ws.impl;

import com.epam.tishkin.server.dao.HistoryManager;
import com.epam.tishkin.server.dao.LibraryDAO;
import com.epam.tishkin.server.dao.impl.LibraryDAOImpl;
import com.epam.tishkin.models.*;
import com.epam.tishkin.server.ws.LibraryVisitor;
import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

import java.io.File;
import java.util.List;
import java.util.Map;

@WebService(endpointInterface = "com.epam.tishkin.server.ws.LibraryVisitor")
public class LibraryVisitorImpl implements LibraryVisitor {
    private final LibraryDAO libraryDAO = new LibraryDAOImpl();

    @Resource
    WebServiceContext webServiceContext;

    public Role userAuthorization() {
        MessageContext messageContext = webServiceContext.getMessageContext();
        Map<?, ?> http_headers = (Map<?, ?>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        List<?> userList = (List<?>) http_headers.get("Username");
        List<?> passList = (List<?>) http_headers.get("Password");
        String login = "";
        String password = "";
        if(userList != null) {
            login = userList.get(0).toString();
        }
        if(passList != null) {
            password = passList.get(0).toString();
        }
      return libraryDAO.userAuthorization(login, password);
    }

    public boolean addUser(String login, String password) {
        if (libraryDAO.addUser(login, password)) {
            HistoryManager.write(getLogin(), "New user added - " + login);
            return true;
        }
        return false;
    }

    public boolean blockUser(String login) {
        if (libraryDAO.blockUser(login)) {
            HistoryManager.write(getLogin(), "User deleted - " + login);
            return true;
        }
        return false;
    }

    public boolean addBookmark(String bookTitle, int pageNumber) {
        String currentLogin = getLogin();
        if (libraryDAO.addBookmark(currentLogin, bookTitle, pageNumber)) {
            HistoryManager.write(currentLogin, "Bookmark was added to the "
                    + bookTitle + " book on the page " + pageNumber);
            return true;
        }
        return false;
    }

    public boolean deleteBookmark(String bookTitle) {
        String userLogin = getLogin();
        if (libraryDAO.deleteBookmark(userLogin, bookTitle)) {
            HistoryManager.write(userLogin, "Bookmark deleted - book title: " + bookTitle);
            return true;
        }
        return false;
    }

    public List<Bookmark> showBooksWithBookmarks() {
        String userLogin = getLogin();
        return libraryDAO.showBooksWithBookmarks(userLogin);
    }

    public List<String> showHistory() {
        return HistoryManager.read();
    }

    public boolean addBook(String title, String ISBNumber, int year, int pages, String bookAuthor) {
        if (libraryDAO.addBook(title, ISBNumber, year, pages, bookAuthor)) {
            HistoryManager.write(getLogin(), "Book added - " + title);
            return true;
        }
        return false;
    }

    public boolean deleteBook(String title, String authorName) {
        if (libraryDAO.deleteBook(title, authorName)) {
            HistoryManager.write(getLogin(), "Book removed - " + title);
            return true;
        }
        return false;
    }

    public boolean addAuthor(String authorName) {
        if (libraryDAO.addAuthor(authorName)) {
            HistoryManager.write(getLogin(), "Author added - " + authorName);
            return true;
        }
        return false;
    }

    public boolean deleteAuthor(String authorName) {
        if (libraryDAO.deleteAuthor(authorName)) {
            HistoryManager.write(getLogin(), "Author removed - " + authorName);
            return true;
        }
        return false;
    }

    public int addBooksFromCatalog(File file) {
        int booksAdded = libraryDAO.addBooksFromCatalog(file);
        HistoryManager.write(getLogin(), "Books added from JSON catalog - " + booksAdded);
        return booksAdded;
    }

    public List<Book> searchBookForTitle(String title) {
        return libraryDAO.searchBookForTitle(title);
    }

    public List<Book> searchBooksForAuthor(String authorName) {
       return libraryDAO.searchBooksForAuthor(authorName);
    }

    public Book searchBookForISBN(String ISBNumber) {
        return libraryDAO.searchBookForISBN(ISBNumber);
    }

    public List<Book> searchBooksByYearRange(int startYear, int finishYear) {
       return libraryDAO.searchBooksByYearRange(startYear, finishYear);
    }

    public List<Book> searchBookByYearPagesNumberAndTitle(int year, int pages, String title) {
       return libraryDAO.searchBookByYearPagesNumberAndTitle(year, pages, title);
    }

    public Book findBookByFullTitle(String title) {
       return libraryDAO.findBookByFullTitle(title);
    }

    private String getLogin() {
        MessageContext messageContext = webServiceContext.getMessageContext();
        Map<?, ?> http_headers = (Map<?, ?>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        List<?> userList = (List<?>) http_headers.get("Username");
        return userList.get(0).toString();
    }
}
