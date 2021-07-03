package com.epam.tishkin.ws;

import com.epam.tishkin.models.Bookmark;
import com.epam.tishkin.models.Role;
import com.epam.tishkin.models.User;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

import java.util.List;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface Visitor {
    @WebMethod Role userAuthorization();
    @WebMethod boolean addUser(String login, String password);
    @WebMethod boolean blockUser(String login);
    @WebMethod List<String> showHistory();
    @WebMethod boolean addBookmark(String bookTitle, int pageNumber);
    @WebMethod boolean deleteBookmark(String bookTitle);
    @WebMethod List<Bookmark> showBooksWithBookmarks();
}
