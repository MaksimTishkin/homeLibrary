package com.epam.tishkin.ws;

import com.epam.tishkin.models.Book;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

import java.io.File;
import java.util.List;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface Library {
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
