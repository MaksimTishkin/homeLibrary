package com.epam.tishkin.server.rs.resource;

import com.epam.tishkin.models.Bookmark;
import com.epam.tishkin.models.BookmarksList;
import com.epam.tishkin.models.HistoryList;
import com.epam.tishkin.models.User;
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
    TokenManager tokenManager = new TokenManager();

    @Inject
    private LibraryService libraryService;

    @GET
    @Path("/authorization")
    public Response authorization(
            @CookieParam("login") String login,
            @CookieParam("password") String password) {
       User user = libraryService.authorization(login, password);
       if (user == null) {
           return Response.status(401).build();
       }
       String jwt = tokenManager.createToken(user);
       return Response.status(200).cookie(new NewCookie("token", jwt)).build();
    }

    @GET
    @Path("/role")
    public Response getRole(@CookieParam("token") Cookie jwt) {
        String role = tokenManager.getRoleFromJWT(jwt.getValue());
        return Response.status(200).entity(role).build();
    }

    @UserAuth
    @GET
    @Path("/show-bookmarks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response showBooksWithBookmarks(@CookieParam("taken") String jwt) {
        String login = tokenManager.getLoginFromJWT(jwt);
        BookmarksList list = new BookmarksList();
        List<Bookmark> foundBookmarks = libraryService.showBooksWithBookmarks(login);
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
    @POST
    @Path("/delete/{login}")
    public Response addUser(@PathParam("login") String login) {
        if (libraryService.deleteUser(login)) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @UserAuth
    @UserRole
    @POST
    @Path("/history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response showHistory() {
        List<String> history = libraryService.showHistory();
        HistoryList list = new HistoryList();
        list.setHistory(history);
        return Response.status(200).entity(list).build();
    }

}
