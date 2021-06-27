package com.epam.tishkin.ws;

import com.epam.tishkin.models.Book;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface Library {
    @WebMethod boolean addBook(Book book, String authorName);
    @WebMethod boolean deleteBook(String title, String authorName);
    @WebMethod boolean addAuthor(String authorName);
    @WebMethod boolean deleteAuthor(String authorName);
    @WebMethod int addBooksFromCSV(String fileName);
    @WebMethod int addBooksFromJSON(String fileName);
    @WebMethod void searchBookForTitle(String title);
    @WebMethod void searchBooksForAuthor(String authorName);
    @WebMethod boolean searchBookForISBN(String ISBNumber);
    @WebMethod void searchBooksByYearRange(int startYear, int finishYear);
    @WebMethod void searchBookByYearPagesNumberAndTitle(int year, int pages, String title);
    @WebMethod Book findBookByFullTitle(String title);
}
