package com.epam.tishkin.server.rs.controller;

import com.epam.tishkin.models.Bookmark;
import com.epam.tishkin.models.BookmarksList;
import com.epam.tishkin.models.HistoryList;
import com.epam.tishkin.models.User;
import com.epam.tishkin.server.rs.config.HistoryManager;
import com.epam.tishkin.server.rs.config.TokenManager;
import com.epam.tishkin.server.rs.filter.UserAuth;
import com.epam.tishkin.server.rs.filter.UserRole;
import com.epam.tishkin.server.rs.service.LibraryService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/users")
public class UserREST {
    private final TokenManager tokenManager = new TokenManager();

    @Inject
    private LibraryService libraryService;

    @POST
    @Path("/authorization")
    public Response authorization(
            @HeaderParam("login") String login,
            @HeaderParam("password") String password) {
       User user = libraryService.authorization(login, password);
       if (user == null) {
           return Response.status(401).build();
       }
       String jwt = tokenManager.createToken(user);
       HistoryManager.write(login, "is connected");
       return Response.status(200).cookie(new NewCookie(TokenManager.AUTHORIZATION_PROPERTY, jwt)).build();
    }

    @GET
    @Path("/role")
    public Response getRole(@CookieParam(TokenManager.AUTHORIZATION_PROPERTY) Cookie jwt) {
        String role = tokenManager.getRoleFromJWT(jwt.getValue());
        return Response.status(200).entity(role).build();
    }

    @UserAuth
    @GET
    @Path("/get-bookmarks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookmarks(@CookieParam(TokenManager.AUTHORIZATION_PROPERTY) String jwt) {
        String login = tokenManager.getLoginFromJWT(jwt);
        BookmarksList list = new BookmarksList();
        List<Bookmark> foundBookmarks = libraryService.getBookmarks(login);
        list.setBookmarks(foundBookmarks);
        return Response.status(200).entity(list).build();
    }

    @UserAuth
    @UserRole
    @POST
    @Path("/add")
    public Response addUser(User user) {
        if (libraryService.addNewUser(user)) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @UserAuth
    @UserRole
    @DELETE
    @Path("/delete/{login}")
    public Response deleteUser(@PathParam("login") String login) {
        if (libraryService.deleteUser(login)) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @UserAuth
    @UserRole
    @GET
    @Path("/history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response showHistory() {
        List<String> history = libraryService.showHistory();
        HistoryList list = new HistoryList();
        list.setHistory(history);
        return Response.status(200).entity(list).build();
    }
}
