package com.epam.tishkin.client;

import com.epam.tishkin.client.exception.AccessDeniedException;
import com.epam.tishkin.models.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.util.List;

public class ClientServiceREST {
    private static final String REST_URI = "http://localhost:8083/homeLibrary/";
    private static final String AUTHORIZATION_PROPERTY = "token";
    private final Client client = ClientBuilder.newClient();

    public String authorization(String login, String password) {
        Response response = client
                .target(REST_URI)
                .path("users/authorization/")
                .request(MediaType.TEXT_PLAIN)
                .cookie("login", login)
                .cookie("password", password)
                .get();
        if (response.getStatus() == 401) {
            return null;
        }
        return response.getCookies().get(AUTHORIZATION_PROPERTY).getValue();
    }

    public String getRole(String jwt) {
        Response response = client
                .target(REST_URI)
                .path("users/role")
                .request(MediaType.TEXT_PLAIN)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .get();
        return response.readEntity(String.class);
    }

    public boolean addNewBook(String bookTitle, String ISBNumber, int publicationYear,
                              int pagesNumber, String bookAuthor, String jwt) throws AccessDeniedException {
        Book book = new Book(bookTitle, ISBNumber, publicationYear, pagesNumber, new Author(bookAuthor));
        Response response = client
                .target(REST_URI)
                .path("books/add")
                .request(MediaType.TEXT_PLAIN)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .post(Entity.entity(book, MediaType.APPLICATION_JSON));
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.getStatus() == 200;
    }

    public boolean deleteBook(String title, String authorName, String jwt) throws AccessDeniedException {
        Response response = client
                .target(REST_URI)
                .path("books/delete/" + authorName + "/" + title)
                .request(MediaType.TEXT_PLAIN)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .delete();
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.getStatus() == 200;
    }

    public boolean addAuthor(String authorName, String jwt) throws AccessDeniedException {
        Author author = new Author(authorName);
        Response response = client
                .target(REST_URI)
                .path("authors/add")
                .request(MediaType.TEXT_PLAIN)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .post(Entity.entity(author, MediaType.APPLICATION_JSON));
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.getStatus() == 200;
    }

    public boolean deleteAuthor(String authorName, String jwt) throws AccessDeniedException {
        Response response = client
                .target(REST_URI)
                .path("authors/delete" + authorName)
                .request(MediaType.TEXT_PLAIN)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .delete();
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.getStatus() == 200;
    }

    public Integer addBooksFromCatalog(File file, String jwt) throws AccessDeniedException {
        Response response = client
                .target(REST_URI)
                .path("books/add-from-catalog")
                .request(MediaType.TEXT_PLAIN)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .post(Entity.entity(file, MediaType.APPLICATION_OCTET_STREAM));
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        System.out.println();
        return response.readEntity(Integer.class);
    }

    public List<Book> searchBookForTitle(String title, String jwt) throws AccessDeniedException {
        Response response = client
                .target(REST_URI)
                .path("books/search-for-title/" + title)
                .request(MediaType.APPLICATION_JSON)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .get();
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.readEntity(BooksList.class).getBooks();
    }

    public List<Book> searchBooksForAuthor(String bookAuthor, String jwt) throws AccessDeniedException {
        Response response = client
                .target(REST_URI)
                .path("books/search-for-author/" + bookAuthor)
                .request(MediaType.APPLICATION_JSON)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .get();
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.readEntity(BooksList.class).getBooks();
    }

    public Book searchBookForISBN(String isbn, String jwt) throws AccessDeniedException {
        Response response = client
                .target(REST_URI)
                .path("books/search-for-isbn/" + isbn)
                .request(MediaType.APPLICATION_JSON)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .get();
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.readEntity(Book.class);
    }

    public List<Book> searchBooksByYearRange(int startYear, int finishYear, String jwt) throws AccessDeniedException {
        Response response = client
                .target(REST_URI)
                .path("books/search-for-years/" + startYear + "/" + finishYear)
                .request(MediaType.APPLICATION_JSON)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .get();
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.readEntity(BooksList.class).getBooks();
    }

    public List<Book> searchBookByYearPagesNumberAndTitle(int year, int pages, String title, String jwt) throws AccessDeniedException {
        Response response = client
                .target(REST_URI)
                .path("books/search-for-year-pages-title/" + year + "/" + pages + "/" + title)
                .request(MediaType.APPLICATION_JSON)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .get();
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.readEntity(BooksList.class).getBooks();
    }

    public Book findBookByFullTitle(String bookTitle, String jwt) throws AccessDeniedException {
        Response response = client
                .target(REST_URI)
                .path("books/search-by-full-title/" + bookTitle)
                .request(MediaType.APPLICATION_JSON)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .get();
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.readEntity(Book.class);
    }

    public boolean addBookmark(String bookTitle, int page, String jwt) throws AccessDeniedException {
        Bookmark bookmark = new Bookmark(bookTitle, page);
        Response response = client
                .target(REST_URI)
                .path("bookmarks/add")
                .request(MediaType.TEXT_PLAIN)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .post(Entity.entity(bookmark, MediaType.APPLICATION_JSON));
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.getStatus() == 200;
    }

    public boolean deleteBookmark(String bookTitle, String jwt) throws AccessDeniedException {
        Response response = client
                .target(REST_URI)
                .path("bookmarks/delete/" + bookTitle)
                .request(MediaType.TEXT_PLAIN)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .delete();
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.getStatus() == 200;
    }

    public List<Bookmark> showBooksWithBookmarks(String jwt) throws AccessDeniedException {
        Response response = client
                .target(REST_URI)
                .path("users/show-bookmarks")
                .request(MediaType.APPLICATION_JSON)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .get();
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.readEntity(BookmarksList.class).getBookmarks();
    }

    public boolean addUser(String login, String password, String jwt) throws AccessDeniedException {
        User user = new User(login, password, Role.VISITOR);
        Response response = client
                .target(REST_URI)
                .path("users/add")
                .request(MediaType.TEXT_PLAIN)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON));
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.getStatus() == 200;
    }

    public boolean blockUser(String login, String jwt) throws AccessDeniedException {
        Response response = client
                .target(REST_URI)
                .path("users/delete/" + login)
                .request(MediaType.TEXT_PLAIN)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .delete();
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.getStatus() == 200;
    }

    public List<String> showHistory(String jwt) throws AccessDeniedException {
        Response response = client
                .target(REST_URI)
                .path("users/history")
                .request(MediaType.APPLICATION_JSON)
                .cookie(AUTHORIZATION_PROPERTY, jwt)
                .get();
        if (response.getStatus() == 403) {
            throw new AccessDeniedException("access denied");
        }
        return response.readEntity(HistoryList.class).getHistory();
    }
}
