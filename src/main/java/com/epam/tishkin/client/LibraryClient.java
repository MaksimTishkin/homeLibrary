package com.epam.tishkin.client;

import com.epam.tishkin.models.Bookmark;
import com.epam.tishkin.ws.*;
import com.epam.tishkin.models.Book;
import com.epam.tishkin.models.Role;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.handler.MessageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Year;
import java.util.*;

public class LibraryClient {
    final static Logger logger = LogManager.getLogger(LibraryClient.class);
    private LibraryVisitor libraryVisitor;
    private Role role;

    public static void main(String[] args) {
        new LibraryClient().run();
    }

    private void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            connection();
            while ((role = authorization(reader)) == null) {
                logger.info("Incorrect login/password");
            }
            startLibraryUse(reader);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void connection() {
        URL url = null;
        try {
            url = new URL("http://localhost:9999/ws/library?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        QName qname = new QName("http://impl.ws.tishkin.epam.com/", "LibraryVisitorImplService");
        Service service = Service.create(url, qname);
        libraryVisitor = service.getPort(LibraryVisitor.class);
    }

    private Role authorization(BufferedReader reader) throws IOException {
        String WS_URL = "http://localhost:9999/ws/library?wsdl";
        System.out.println("Enter your login");
        String login = reader.readLine();
        System.out.println("Enter your password");
        String password = reader.readLine();
        Map<String, Object> req_ctx = ((BindingProvider) libraryVisitor).getRequestContext();
        req_ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, WS_URL);

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Username", Collections.singletonList(login));
        headers.put("Password", Collections.singletonList(password));
        req_ctx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        return libraryVisitor.userAuthorization();
    }

    public void startLibraryUse(BufferedReader reader) throws IOException {
        while(true) {
            System.out.println("1 Add a new book");
            System.out.println("2 Delete book");
            System.out.println("3 Add a new author");
            System.out.println("4 Delete author");
            System.out.println("5 Add books from CSV or JSON catalog");
            System.out.println("6 Find a book by title");
            System.out.println("7 Add a bookmark to a book");
            System.out.println("8 Delete a bookmark from a book");
            System.out.println("9 Find books by author");
            System.out.println("10 Find book by ISBN number");
            System.out.println("11 Find books by year range");
            System.out.println("12 Find a book by year, number of pages, and title");
            System.out.println("13 Find books with my bookmark");
            System.out.println("14 Exit");
            if (role == Role.ADMINISTRATOR) {
                System.out.println("15 Settings (for administrators only)");
            }
            String request = reader.readLine();
            switch (request) {
                case "1":
                    addNewBook(reader);
                    break;
                case "2":
                    deleteBook(reader);
                    break;
                case "3":
                    addAuthor(reader);
                    break;
                case "4":
                    deleteAuthor(reader);
                    break;
                case "5":
                    addBooksFromCatalog(reader);
                    break;
                case "6":
                    searchBookForTitle(reader);
                    break;
                case "7":
                    addBookmark(reader);
                    break;
                case "8":
                    deleteBookmark(reader);
                    break;
                case "9":
                    searchBooksForAuthor(reader);
                    break;
                case "10":
                    searchBookForISBNumber(reader);
                    break;
                case "11":
                    searchBooksForYearRange(reader);
                    break;
                case "12":
                    searchBookByYearPagesNumberAndTitle(reader);
                    break;
                case "13":
                    showBooksWithBookmarks();
                    break;
                case "14":
                    return;
                case "15":
                    useAdditionalAdministratorFeatures(reader);
                    break;
                }
            }
        }

    private void addNewBook(BufferedReader reader) throws IOException {
        Integer publicationYear;
        String ISBNumber;
        Integer pagesNumber;
        System.out.println("Enter the book title");
        String bookTitle = reader.readLine();
        if (bookTitle.isEmpty()) {
            logger.info("Book title is empty");
            return;
        }
        System.out.println("Enter the author of the book");
        String bookAuthor = reader.readLine();
        if (bookAuthor.isEmpty()) {
            logger.info("The author is empty");
            return;
        }
        System.out.println("Enter the book ISBN number");
        ISBNumber = reader.readLine();
        if (!isISBNumberCorrect(ISBNumber)) {
            logger.info("Incorrect ISBNumber: " + ISBNumber);
            return;
        }
        System.out.println("Enter the year of publication");
        String year = reader.readLine();
        publicationYear = checkYearOfPublication(year);
        if (publicationYear == null) {
            logger.info("Incorrect year of publication: " + year);
            return;
        }
        System.out.println("Enter the number of pages");
        String number = reader.readLine();
        pagesNumber = checkNumberOfPages(number);
        if (pagesNumber == null) {
            logger.info("Invalid page value: " + number);
            return;
        }
        if (libraryVisitor.addBook(bookTitle, ISBNumber, publicationYear, pagesNumber, bookAuthor)) {
            logger.info("New book added - " + bookTitle);
        } else {
            logger.info(bookTitle + ": this book is already in the database");
        }
    }

    private void deleteBook(BufferedReader reader) throws IOException {
        System.out.println("Enter the book title you want to delete");
        String bookTitle = reader.readLine();
        System.out.println("Enter the book author");
        String bookAuthor = reader.readLine();
        if (libraryVisitor.deleteBook(bookTitle, bookAuthor)) {
            logger.info("Book deleted: " + bookTitle);
        } else {
            logger.info("Book not found: " + bookTitle);
        }
    }

    private void addAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter the author's name");
        String bookAuthor = reader.readLine();
        if (libraryVisitor.addAuthor(bookAuthor)) {
            logger.info("New author added: " + bookAuthor);
        } else {
            logger.info("This author is already in the database: " + bookAuthor);
        }
    }

    private void deleteAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter the author's name");
        String bookAuthor = reader.readLine();
        if (libraryVisitor.deleteAuthor(bookAuthor)) {
            logger.info("Author deleted: " + bookAuthor);
        } else {
            logger.info("Author not found: " + bookAuthor);
        }
    }

    private void addBooksFromCatalog(BufferedReader reader) throws IOException {
        System.out.println("Enter the path to the folder");
        String filePath = reader.readLine();
        if (isFileExtensionCorrect(filePath)) {
            File file = new File(filePath);
            int booksAdded = libraryVisitor.addBooksFromCatalog(file);
            logger.info("Number of books added from catalog: " + booksAdded);
        } else {
            logger.info("Incorrect file's type");
        }

    }

    private boolean isFileExtensionCorrect(String path) {
        int index = path.lastIndexOf('.');
        String extension = path.substring(index + 1);
        return "json".equals(extension) || "csv".equals(extension);
    }

    private void searchBookForTitle(BufferedReader reader) throws IOException {
        System.out.println("Enter part of the book title");
        String bookTitle = reader.readLine();
        List<Book> foundBooks = libraryVisitor.searchBookForTitle(bookTitle);
        if (foundBooks.isEmpty()) {
            logger.info("No books found");
        } else {
            foundBooks.forEach(logger::info);
        }
    }

    private void searchBooksForAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter part of the author's name");
        String bookAuthor = reader.readLine();
        List<Book> foundBooks = libraryVisitor.searchBooksForAuthor(bookAuthor);
        if (foundBooks.isEmpty()) {
            logger.info("No books found");
        } else {
            foundBooks.forEach(logger::info);
        }
    }

    private void searchBookForISBNumber(BufferedReader reader) throws IOException {
        System.out.println("Enter book's ISBN number");
        String number = reader.readLine();
        Book foundBook = libraryVisitor.searchBookForISBN(number);
        if (foundBook == null) {
            logger.info("No books found");
        } else {
            logger.info(foundBook);
        }
    }

    private void searchBooksForYearRange(BufferedReader reader) throws IOException {
        System.out.println("Enter the initial year value");
        String firstValue = reader.readLine();
        System.out.println("Enter the final year value");
        String secondValue = reader.readLine();
        try {
            int initialYear = Integer.parseInt(firstValue);
            int finalYear = Integer.parseInt(secondValue);
            if (initialYear > finalYear) {
                logger.info("Incorrect year specified: initial year " + initialYear + " final year " + finalYear);
                return;
            }
            List<Book> foundBooks = libraryVisitor.searchBooksByYearRange(initialYear, finalYear);
            if (foundBooks.isEmpty()) {
                logger.info("No books found");
            } else {
                foundBooks.forEach(logger::info);
            }
        } catch (NumberFormatException e) {
            logger.info("Incorrect year specified: initial year " + firstValue + " final year " + secondValue);
        }
    }

    private void searchBookByYearPagesNumberAndTitle(BufferedReader reader) throws IOException {
        try {
            System.out.println("Enter the year value");
            int year = Integer.parseInt(reader.readLine());
            System.out.println("Enter the pages number");
            int pagesNumber= Integer.parseInt(reader.readLine());
            System.out.println("Enter part of the book title");
            String bookTitle = reader.readLine();
            List<Book> foundBooks = libraryVisitor.searchBookByYearPagesNumberAndTitle(year, pagesNumber, bookTitle);
            if (foundBooks.isEmpty()) {
                logger.info("No books found");
            } else {
                logger.info(foundBooks);
            }
        } catch (NumberFormatException e) {
            logger.info("Incorrect input data");
        }
    }
    private void addBookmark(BufferedReader reader) throws IOException {
        System.out.println("Enter the book title");
        String bookTitle = reader.readLine();
        Book book = libraryVisitor.findBookByFullTitle(bookTitle);
        if (book == null) {
            logger.info(bookTitle + " book not found");
            return;
        }
        System.out.println("Enter the page number");
        String page = reader.readLine();
        Integer pageNumber = checkNumberOfPages(page);
        if (pageNumber == null || pageNumber >= book.getPagesNumber()) {
            logger.info("Invalid page value - " + pageNumber);
            return;
        }
        if (!libraryVisitor.addBookmark(bookTitle, pageNumber)) {
            logger.info("The bookmark already exists in this book");
            return;
        }
        logger.info("Bookmark was added to the " + bookTitle + " book on the page " + pageNumber);
    }

    private void deleteBookmark(BufferedReader reader) throws IOException {
        System.out.println("Enter the book title");
        String bookTitle = reader.readLine();
        if (!libraryVisitor.deleteBookmark(bookTitle)) {
            logger.info("There is no bookmark in this book: " + bookTitle);
            return;
        }
        logger.info("Bookmark deleted - book title: " + bookTitle);
    }

    private void showBooksWithBookmarks() {
        List<Bookmark> foundBookmarks = libraryVisitor.showBooksWithBookmarks();
        if (foundBookmarks.isEmpty()) {
            logger.info("There are no bookmarks");
        }
        foundBookmarks.forEach(logger::info);
    }

    private boolean isISBNumberCorrect(String number) {
        if (number.length() != 13) {
            return false;
        }
        try {
            Long.parseLong(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private Integer checkYearOfPublication(String year) {
        int publicationYear;
        try {
            publicationYear = Integer.parseInt(year);
            int currentYear = Year.now().getValue();
            int yearOfFirstBookInWorld = 1457;
            if (publicationYear < yearOfFirstBookInWorld || publicationYear > currentYear) {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return publicationYear;
    }

    private Integer checkNumberOfPages(String number) {
        int pagesNumber;
        try {
            pagesNumber = Integer.parseInt(number);
            if (pagesNumber <= 0) {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return pagesNumber;
    }

    private void useAdditionalAdministratorFeatures(BufferedReader reader) throws IOException {
        if (role != Role.ADMINISTRATOR) {
            return;
        }
        System.out.println("1 Add a new user");
        System.out.println("2 Block user");
        System.out.println("3 Show history");
        System.out.println("4 Go back to the previous menu");
        String request = reader.readLine();
        switch (request) {
            case "1":
                addUser(reader);
                break;
            case "2":
                blockUser(reader);
                break;
            case "3":
                showHistory();
                break;
            case "4":
                break;
        }
    }

    private void addUser(BufferedReader reader) throws IOException {
        if (role != Role.ADMINISTRATOR) {
            return;
        }
        System.out.println("Enter user login");
        String login = reader.readLine();
        System.out.println("Enter user password");
        String password = reader.readLine();
        if (!libraryVisitor.addUser(login, password)) {
            logger.info("This user already exists - " + login);
            return;
        }
        logger.info("New user added - " + login);
    }

    private void blockUser(BufferedReader reader) throws IOException {
        if (role != Role.ADMINISTRATOR) {
            return;
        }
        System.out.println("Enter user login");
        String login = reader.readLine();
        if(!libraryVisitor.blockUser(login)) {
            logger.info("User does not exist - " + login);
            return;
        }
        logger.info("User deleted - " + login);
    }

    private void showHistory() {
        if (role != Role.ADMINISTRATOR) {
            return;
        }
        List<String> fullHistory = libraryVisitor.showHistory();
        fullHistory.forEach(logger::info);
    }
}