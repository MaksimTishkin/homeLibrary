package com.epam.tishkin.server.rs.resource;

import com.epam.tishkin.models.Book;
import com.epam.tishkin.server.rs.filter.UserAuth;
import com.epam.tishkin.server.rs.service.LibraryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/books")
public class BookREST {

    @Inject
    private LibraryService libraryService;

    @UserAuth
    @POST
    @Path("/addBook")
    public Response addNewBook(Book book) {
        if (libraryService.addNewBook(book)) {
            return Response.status(201).build();
        }
        return Response.status(401).build();
    }
}
