package com.epam.tishkin.server.rs.resource;

import com.epam.tishkin.models.Book;
import com.epam.tishkin.models.BooksList;
import com.epam.tishkin.server.rs.filter.UserAuth;
import com.epam.tishkin.server.rs.service.LibraryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.util.List;

@Path("/books")
public class BookREST {

    @Inject
    private LibraryService libraryService;

    @UserAuth
    @POST
    @Path("/add")
    public Response addNewBook(Book book) {
        if (libraryService.addNewBook(book)) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @UserAuth
    @DELETE
    @Path("/delete/{authorName}/{bookTitle}")
    public Response deleteBook(
            @PathParam("authorName") String authorName,
            @PathParam("bookTitle") String bookTitle) {
        if (libraryService.deleteBook(authorName, bookTitle)) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @UserAuth
    @POST
    @Path("/add-from-catalog")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addBooksFromCatalog(File file) {
        int booksAdded = libraryService.addBooksFromCatalog(file);
        return Response.status(200).entity(booksAdded).build();
    }

    @UserAuth
    @GET
    @Path("/search-for-title/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBookForTitle(@PathParam("title") String title) {
        List<Book> books = libraryService.searchBookForTitle(title);
        Book book = books.get(0);
        if (book != null) {
            return Response.ok().entity(book).build();
        }
        return Response.status(404).build();
    }

    @UserAuth
    @GET
    @Path("/search-for-author/{bookAuthor}")
    @Produces
    public Response searchBooksForAuthor(@PathParam("bookAuthor") String authorName) {
        BooksList list = new BooksList();
        List<Book> findBooks = libraryService.searchBooksForAuthor(authorName);
        list.setBooks(findBooks);
        return Response.status(200).entity(list).build();
    }

    @UserAuth
    @GET
    @Path("/search-for-isbn/{isbn}")
    public Response searchBookForISBN(@PathParam("isbn") String isbn) {
        Book book = libraryService.searchBookForISBN(isbn);
        return Response.status(200).entity(book).build();
    }

    @UserAuth
    @GET
    @Path("/search-for-years/{startYear}/{finishYear}")
    public Response searchBooksByYearRange(
            @PathParam("initialYear") Integer startYear,
            @PathParam("finalYear") Integer finishYear) {
        BooksList list = new BooksList();
        List<Book> findBooks = libraryService.searchBooksByYearRange(startYear, finishYear);
        list.setBooks(findBooks);
        return Response.status(200).entity(list).build();
    }

    @UserAuth
    @GET
    @Path("/search-for-year-pages-title/{year}/{pages}/{title}")
    public Response searchBookByYearPagesNumberAndTitle(
            @PathParam("year") Integer year,
            @PathParam("pages") Integer pages,
            @PathParam("title") String title) {
        BooksList list = new BooksList();
        List<Book> findBooks = libraryService.searchBookByYearPagesNumberAndTitle(year, pages, title);
        list.setBooks(findBooks);
        return Response.status(200).entity(list).build();
    }

    @UserAuth
    @GET
    @Path("/search-by-full-title/{bookTitle}")
    public Response findBookByFullTitle(@PathParam("bookTitle") String bookTitle) {
        Book book = libraryService.findBookByFullTitle(bookTitle);
        return Response.status(200).entity(book).build();
    }
}
