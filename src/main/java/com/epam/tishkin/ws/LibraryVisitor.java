package com.epam.tishkin.ws;

import com.epam.tishkin.models.Book;
import com.epam.tishkin.models.Bookmark;
import com.epam.tishkin.models.Role;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

import java.io.File;
import java.util.List;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface LibraryVisitor {
    @WebMethod Role userAuthorization();
    @WebMethod boolean addUser(String login, String password);
    @WebMethod boolean blockUser(String login);
    @WebMethod List<String> showHistory();
    @WebMethod boolean addBookmark(String bookTitle, int pageNumber);
    @WebMethod boolean deleteBookmark(String bookTitle);
    @WebMethod List<Bookmark> showBooksWithBookmarks();
    @WebMethod boolean addBook(String title, String ISBNumber, int year, int pages, String author);
    @WebMethod boolean deleteBook(String title, String authorName);
    @WebMethod boolean addAuthor(String authorName);
    @WebMethod boolean deleteAuthor(String authorName);
    @WebMethod int addBooksFromCatalog(File file);
    @WebMethod List<Book> searchBookForTitle(String title);
    @WebMethod List<Book> searchBooksForAuthor(String authorName);
    @WebMethod Book searchBookForISBN(String ISBNumber);
    @WebMethod List<Book> searchBooksByYearRange(int startYear, int finishYear);
    @WebMethod List<Book> searchBookByYearPagesNumberAndTitle(int year, int pages, String title);
    @WebMethod Book findBookByFullTitle(String title);
}
