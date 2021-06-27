package com.epam.tishkin.ws;

import com.epam.tishkin.models.User;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface Visitor {
    @WebMethod User userAuthorization();
    @WebMethod void addUser(String login, String password);
    @WebMethod void blockUser(String login);
    @WebMethod void showHistory();
    @WebMethod void addBookmark(String bookTitle, int pageNumber, String login);
    @WebMethod boolean deleteBookmark(String bookTitle, String login);
    @WebMethod void showBooksWithBookmarks(String login);
}
