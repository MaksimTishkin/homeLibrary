package com.epam.tishkin.server.rs.controller;

import com.epam.tishkin.models.Author;
import com.epam.tishkin.server.rs.config.HistoryManager;
import com.epam.tishkin.server.rs.config.TokenManager;
import com.epam.tishkin.server.rs.filter.UserAuth;
import com.epam.tishkin.server.rs.service.LibraryService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/authors")
public class AuthorREST {
    final TokenManager tokenManager = new TokenManager();

    @Inject
    private LibraryService libraryService;

    @UserAuth
    @POST
    @Path("/add")
    public Response addAuthor(
            @CookieParam(TokenManager.AUTHORIZATION_PROPERTY) String jwt,
            Author author) {
        if (libraryService.addAuthor(author.getName())) {
            String login = tokenManager.getLoginFromJWT(jwt);
            HistoryManager.write(login, "new author added: " + author.getName());
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @UserAuth
    @DELETE
    @Path("/delete/{authorName}")
    public Response deleteAuthor(
            @PathParam("authorName") String authorName,
            @CookieParam(TokenManager.AUTHORIZATION_PROPERTY) String jwt) {
        if (libraryService.deleteAuthor(authorName)) {
            String login = tokenManager.getLoginFromJWT(jwt);
            HistoryManager.write(login, "author deleted: " + authorName);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
}
