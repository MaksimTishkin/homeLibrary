package com.epam.tishkin.client;

import com.epam.tishkin.models.User;
import com.epam.tishkin.ws.*;
import com.epam.tishkin.ws.impl.HistoryWriter;
import com.epam.tishkin.ws.impl.LibraryImpl;
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
    private static final Properties properties = new Properties();
    final static Logger logger = LogManager.getLogger(LibraryClient.class);
    private Visitor visitor;
    private Library library;
    private User user;

    public static void main(String[] args) {
        new LibraryClient().run();
    }

    private void run() {
        libraryConnection();
        visitorConnection();
        while((user = authorization()) == null) {
            System.out.println("Incorrect login/password");
        }
        startLibraryUse();
    }

    private void visitorConnection() {
        URL url = null;
        try {
            url = new URL("http://localhost:9999/ws/user?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        QName qname = new QName("http://impl.ws.tishkin.epam.com/", "VisitorImplService");
        Service service = Service.create(url, qname);
        visitor = service.getPort(Visitor.class);
    }

    private void libraryConnection() {
        URL url = null;
        try {
            url = new URL("http://localhost:9999/ws/library?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        QName qname = new QName("http://impl.ws.tishkin.epam.com/", "LibraryImplService");
        Service service = Service.create(url, qname);
        library = service.getPort(Library.class);
    }

    private User authorization() {
        String WS_URL = "http://localhost:9999/ws/user?wsdl";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter your login");
            String login = reader.readLine();
            System.out.println("Enter your password");
            String password = reader.readLine();
            Map<String, Object> req_ctx = ((BindingProvider)visitor).getRequestContext();
            req_ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, WS_URL);

            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Username", Collections.singletonList(login));
            headers.put("Password", Collections.singletonList(password));
            req_ctx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return visitor.userAuthorization();
    }

    public void startLibraryUse() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
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
                if (user.getRole() == Role.ADMINISTRATOR) {
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
        catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void addNewBook(BufferedReader reader) throws IOException {
        Integer publicationYear;
        String ISBNumber;
        Integer pagesNumber;
        Book currentBook;
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
        currentBook = new Book(bookTitle, ISBNumber, publicationYear, pagesNumber);
        if (library.addBook(currentBook, bookAuthor)) {
            logger.info("New book added - " + currentBook);
            HistoryWriter.write(user.getLogin(), "New book added - " + currentBook);
        } else {
            logger.info(bookTitle + ": this book is already in the database");
        }
    }

    private void deleteBook(BufferedReader reader) throws IOException {
        System.out.println("Enter the book title you want to delete");
        String bookTitle = reader.readLine();
        System.out.println("Enter the book author");
        String bookAuthor = reader.readLine();
        if (library.deleteBook(bookTitle, bookAuthor)) {
            logger.info("Book deleted: " + bookTitle);
        } else {
            logger.info("Book not found: " + bookTitle);
        }
    }

    private void addAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter the author's name");
        String bookAuthor = reader.readLine();
        if (library.addAuthor(bookAuthor)) {
            logger.info("New author added: " + bookAuthor);
        } else {
            logger.info("This author is already in the database: " + bookAuthor);
        }
    }

    private void deleteAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter the author's name");
        String bookAuthor = reader.readLine();
        if (library.deleteAuthor(bookAuthor)) {
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
            int booksAdded = library.addBooksFromCSV(file);
            logger.info("Number of books added from catalog: " + booksAdded);
        } else {
            logger.info("Incorrect file's type");
        }
        //writeToHistory(user.getLogin() + ": number of books added from CSV catalog: " + booksAdded);
    }

    private boolean isFileExtensionCorrect(String path) {
        int index = path.lastIndexOf('.');
        String extension = path.substring(index + 1);
        return "json".equals(extension) || "csv".equals(extension);
    }

    private void searchBookForTitle(BufferedReader reader) throws IOException {
        System.out.println("Enter part of the book title");
        String bookTitle = reader.readLine();
        library.searchBookForTitle(bookTitle);
        writeToHistory(visitor.getUser().getLogin() + ": search for books by title: " + bookTitle);
        }

    private void searchBooksForAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter part of the author's name");
        String bookAuthor = reader.readLine();
        library.searchBooksForAuthor(bookAuthor);
        writeToHistory(visitor.getUser().getLogin() + ": find books by author: " + bookAuthor);
    }

    private void searchBookForISBNumber(BufferedReader reader) throws IOException {
        System.out.println("Enter book's ISBN number");
        String number = reader.readLine();
        if (!isISBNumberCorrect(number)) {
            logger.info("Incorrect ISBNumber: " + number);
            return;
        }
        if (library.searchBookForISBN(number)) {
            writeToHistory(visitor.getUser().getLogin() + ": Book found by ISBN: " + number);
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
            library.searchBooksByYearRange(initialYear, finalYear);
            writeToHistory(visitor.getUser().getLogin() + ": find books by year range " + initialYear + " - " + finalYear);
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
            library.searchBookByYearPagesNumberAndTitle(year, pagesNumber, bookTitle);
            writeToHistory(visitor.getUser().getLogin() + ": find books by year, pages and title ");
        } catch (NumberFormatException e) {
            logger.info("Incorrect input data");
        }
    }
    public void addBookmark(BufferedReader reader) throws IOException {
        System.out.println("Enter the book title");
        String bookTitle = reader.readLine();
        Book book = library.findBookByFullTitle(bookTitle);
        if (book == null) {
            logger.info(bookTitle + " book not found");
            return;
        }
        System.out.println("Enter the page number");
        String page = reader.readLine();
        Integer pageNumber = checkNumberOfPages(page);
        if (pageNumber == null || pageNumber > book.getPagesNumber()) {
            logger.info(pageNumber + " Invalid page value");
            return;
        }
        visitor.addBookmark(bookTitle, pageNumber);
        writeToHistory(visitor.getUser().getLogin() + ": Bookmark added - book title: " + bookTitle + " page: " + pageNumber);
    }

    private void deleteBookmark(BufferedReader reader) throws IOException {
        System.out.println("Enter the book title");
        String bookTitle = reader.readLine();
        if (visitor.deleteBookmark(bookTitle)) {
            writeToHistory(visitor.getUser().getLogin() + ": Bookmark deleted - book title: " + bookTitle);
        }
    }

    private void showBooksWithBookmarks() {
        visitor.showBooksWithBookmarks();
        writeToHistory(visitor.getUser().getLogin() + ": Show books with visitor's bookmark");
    }

    private static void writeToHistory (String message){
        try (FileReader readerForProperties = new FileReader("src/main/resources/config.properties")) {
            properties.load(readerForProperties);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        try (FileWriter writer = new FileWriter(properties.getProperty("pathFromHistory"), true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
       }
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
        System.out.println("Enter user login");
        String login = reader.readLine();
        System.out.println("Enter user password");
        String password = reader.readLine();
        visitor.addUser(login, password);
    }

    private void blockUser(BufferedReader reader) throws IOException {
        System.out.println("Enter user login");
        String login = reader.readLine();
        visitor.blockUser(login);
    }

    private void showHistory() {
        visitor.showHistory();
    }
}