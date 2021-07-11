package com.epam.tishkin.client;

import com.epam.tishkin.models.Author;
import com.epam.tishkin.models.Book;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClientBuilder;

public class ClientServiceREST {
    private static final String REST_URI = "http://localhost:8083/homeLibrary/";
    private static final String AUTHORIZATION_PROPERTY = "token";
    private final Client client = JerseyClientBuilder.createClient();

    public String authorization(String login, String password) {
        Response response = client
                .target(REST_URI)
                .path("users/authorization/")
                .request(MediaType.TEXT_PLAIN)
                .header("login", login)
                .header("password", password)
                .get();
        if (response.getStatus() == 401) {
            return null;
        }
        return response.readEntity(String.class);
    }

    public boolean addNewBook(String bookTitle, String ISBNumber, int publicationYear,
                              int pagesNumber, String bookAuthor, String jwt) {
        Book book = new Book(bookTitle, ISBNumber, publicationYear, pagesNumber, new Author(bookAuthor));
        Response response = client
                .target(REST_URI)
                .path("books/addBook")
                .request(MediaType.TEXT_PLAIN)
                .header(AUTHORIZATION_PROPERTY, jwt)
                .post(Entity.entity(book, MediaType.APPLICATION_JSON));
        return response.getStatus() == 201;
    }

    public String getRole(String jwt) {
        Response response = client
                .target(REST_URI)
                .path("users/role")
                .request(MediaType.TEXT_PLAIN)
                .header(AUTHORIZATION_PROPERTY, jwt)
                .get();
        return response.readEntity(String.class);
    }
}
