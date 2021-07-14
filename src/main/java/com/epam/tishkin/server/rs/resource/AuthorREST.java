package com.epam.tishkin.server.rs.resource;

import com.epam.tishkin.models.Author;
import com.epam.tishkin.server.rs.filter.UserAuth;
import com.epam.tishkin.server.rs.service.LibraryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/authors")
public class AuthorREST {

    @Inject
    private LibraryService libraryService;

    @UserAuth
    @POST
    @Path("/add")
    public Response addAuthor(Author author) {
        if (libraryService.addAuthor(author.getName())) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @UserAuth
    @DELETE
    @Path("/delete/{authorName}")
    public Response deleteAuthor(@PathParam("authorName") String authorName) {
        if (libraryService.deleteAuthor(authorName)) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
}
