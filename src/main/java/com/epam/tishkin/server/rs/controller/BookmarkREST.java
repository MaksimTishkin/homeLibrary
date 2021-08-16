package com.epam.tishkin.server.rs.controller;

import com.epam.tishkin.models.Bookmark;
import com.epam.tishkin.server.rs.config.HistoryManager;
import com.epam.tishkin.server.rs.config.TokenManager;
import com.epam.tishkin.server.rs.filter.UserAuth;
import com.epam.tishkin.server.rs.service.LibraryService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/bookmarks")
public class BookmarkREST {
    private final TokenManager tokenManager = new TokenManager();

    @Inject
    private LibraryService libraryService;

    @UserAuth
    @POST
    @Path("/add")
    public Response addBookmark(
            @CookieParam(TokenManager.AUTHORIZATION_PROPERTY) String jwt,
            Bookmark bookmark) {
        String login = tokenManager.getLoginFromJWT(jwt);
        if (libraryService.addBookmark(bookmark, login)) {
            HistoryManager.write(login, "bookmark added to the " + bookmark.getTitle()
                    + " book on the page " + bookmark.getPage());
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @UserAuth
    @DELETE
    @Path("/delete/{bookTitle}")
    public Response deleteBookmark(
            @PathParam("bookTitle") String bookTitle,
            @CookieParam(TokenManager.AUTHORIZATION_PROPERTY) String jwt) {
        String login = tokenManager.getLoginFromJWT(jwt);
        if (libraryService.deleteBookmark(bookTitle, login)) {
            HistoryManager.write(login, "bookmark deleted from book: " + bookTitle);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
}
