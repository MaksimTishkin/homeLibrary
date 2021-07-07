package com.epam.tishkin.client;

import com.epam.tishkin.models.Book;
import com.epam.tishkin.models.Bookmark;
import com.epam.tishkin.models.Role;
import com.epam.tishkin.server.ws.LibraryVisitor;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.handler.MessageContext;

import javax.xml.namespace.QName;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientServiceSOAP {
    String WS_URL = "http://localhost:9999/ws/library?wsdl";
    private LibraryVisitor libraryVisitor;


    public void connection() {
        URL url = null;
        try {
            url = new URL(WS_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        QName qname = new QName("http://impl.ws.server.tishkin.epam.com/", "LibraryVisitorImplService");
        Service service = Service.create(url, qname);
        libraryVisitor = service.getPort(LibraryVisitor.class);
    }

    public Role authorization(String login, String password) {
        Map<String, Object> req_ctx = ((BindingProvider) libraryVisitor).getRequestContext();
        req_ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, WS_URL);

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Username", Collections.singletonList(login));
        headers.put("Password", Collections.singletonList(password));
        req_ctx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        return libraryVisitor.userAuthorization();
    }

    public boolean addNewBook(String title, String ISBNumber, int year, int pages, String bookAuthor) {
        return libraryVisitor.addBook(title, ISBNumber, year, pages, bookAuthor);
    }

    public boolean deleteBook(String title, String authorName) {
        return libraryVisitor.deleteBook(title, authorName);
    }

    public boolean addAuthor(String authorName) {
       return libraryVisitor.addAuthor(authorName);
    }

    public boolean deleteAuthor(String authorName) {
       return libraryVisitor.deleteAuthor(authorName);
    }

    public int addBooksFromCatalog(File file) {
        return libraryVisitor.addBooksFromCatalog(file);
    }

    public List<Book> searchBookForTitle(String title) {
        return libraryVisitor.searchBookForTitle(title);
    }

    public List<Book> searchBooksForAuthor(String authorName) {
        return libraryVisitor.searchBooksForAuthor(authorName);
    }

    public Book searchBookForISBN(String ISBNumber) {
        return libraryVisitor.searchBookForISBN(ISBNumber);
    }

    public List<Book> searchBooksByYearRange(int startYear, int finishYear) {
        return libraryVisitor.searchBooksByYearRange(startYear, finishYear);
    }

    public List<Book> searchBookByYearPagesNumberAndTitle(int year, int pages, String title) {
        return libraryVisitor.searchBookByYearPagesNumberAndTitle(year, pages, title);
    }

    public Book findBookByFullTitle(String title) {
        return libraryVisitor.findBookByFullTitle(title);
    }

    public boolean addBookmark(String bookTitle, int pageNumber) {
        return libraryVisitor.addBookmark(bookTitle, pageNumber);
    }

    public boolean deleteBookmark(String bookTitle) {
        return libraryVisitor.deleteBookmark(bookTitle);
    }

    public List<Bookmark> showBooksWithBookmarks() {
        return libraryVisitor.showBooksWithBookmarks();
    }

    public boolean addUser(String login, String password) {
        return libraryVisitor.addUser(login, password);
    }

    public boolean blockUser(String login) {
        return libraryVisitor.blockUser(login);
    }

    public List<String> showHistory() {
        return libraryVisitor.showHistory();
    }
}
