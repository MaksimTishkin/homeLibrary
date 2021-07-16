package com.epam.tishkin.client;

import com.epam.tishkin.client.exception.AccessDeniedException;
import com.epam.tishkin.models.Book;
import com.epam.tishkin.models.Bookmark;
import com.epam.tishkin.server.rs.service.LibraryService;
import com.epam.tishkin.server.rs.service.impl.LibraryServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Year;
import java.util.List;

public class LibraryClient {
    private static final ClientServiceREST clientServiceREST = new ClientServiceREST();
    final static Logger logger = LogManager.getLogger(LibraryClient.class);
    private String jwt;
    private String role;
    LibraryService libraryService = new LibraryServiceImpl();

    public static void main(String[] args) {
        new LibraryClient().run();
    }

    private void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while ((jwt = authorization(reader)) == null) {
                logger.info("Incorrect login/password");
            }
            role = clientServiceREST.getRole(jwt);
            startLibraryUse(reader);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String authorization(BufferedReader reader) throws IOException {
        System.out.println("Enter your login");
        String login = reader.readLine();
        System.out.println("Enter your password");
        String password = reader.readLine();
        return clientServiceREST.authorization(login, password);
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
            if ("ADMINISTRATOR".equals(role)) {
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
        try {
            if (clientServiceREST.addNewBook(bookTitle, ISBNumber, publicationYear, pagesNumber, bookAuthor, jwt)) {
                logger.info("New book added - " + bookTitle);
            } else {
                logger.info(bookTitle + ": this book is already in the database");
            }
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
        }
    }

    private void deleteBook(BufferedReader reader) throws IOException {
        System.out.println("Enter the book title you want to delete");
        String bookTitle = reader.readLine();
        System.out.println("Enter the book author");
        String bookAuthor = reader.readLine();
        try {
            if (clientServiceREST.deleteBook(bookTitle, bookAuthor, jwt)) {
                logger.info("Book deleted: " + bookTitle);
            } else {
                logger.info("Book not found: " + bookTitle);
            }
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
        }
    }

    private void addAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter the author's name");
        String bookAuthor = reader.readLine();
        try {
            if (clientServiceREST.addAuthor(bookAuthor, jwt)) {
                logger.info("New author added: " + bookAuthor);
            } else {
                logger.info("This author is already in the database: " + bookAuthor);
            }
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
        }
    }

    private void deleteAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter the author's name");
        String bookAuthor = reader.readLine();
        try {
            if (clientServiceREST.deleteAuthor(bookAuthor, jwt)) {
                logger.info("Author deleted: " + bookAuthor);
            } else {
                logger.info("Author not found: " + bookAuthor);
            }
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
        }
    }

    private void addBooksFromCatalog(BufferedReader reader) throws IOException {
        System.out.println("Enter the path to the folder");
        String filePath = reader.readLine();
        try {
            if (isFileExtensionCorrect(filePath)) {
                File file = new File(filePath);
                int booksAdded = clientServiceREST.addBooksFromCatalog(file, jwt);
                logger.info("Number of books added from catalog: " + booksAdded);
            } else {
                logger.info("Incorrect file's type");
            }
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
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
        try {
            Book foundBooks = clientServiceREST.searchBookForTitle(bookTitle, jwt);
            if (foundBooks == null) {
                logger.info("No books found");
            } else {
               logger.info(foundBooks);
            }
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
        }
    }

    private void searchBooksForAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter part of the author's name");
        String bookAuthor = reader.readLine();
        try {
            List<Book> foundBooks = clientServiceREST.searchBooksForAuthor(bookAuthor, jwt);
            if (foundBooks.isEmpty()) {
                logger.info("No books found");
            } else {
                foundBooks.forEach(logger::info);
            }
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
        }
    }

    private void searchBookForISBNumber(BufferedReader reader) throws IOException {
        System.out.println("Enter book's ISBN number");
        String isbn = reader.readLine();
        try {
            Book foundBook = clientServiceREST.searchBookForISBN(isbn, jwt);
            if (foundBook == null) {
                logger.info("No books found");
            } else {
                logger.info(foundBook);
            }
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
        }
    }

    private void searchBooksForYearRange(BufferedReader reader) throws IOException {
        System.out.println("Enter the initial year value");
        String firstValue = reader.readLine();
        System.out.println("Enter the final year value");
        String secondValue = reader.readLine();
        try {
            int startlYear = Integer.parseInt(firstValue);
            int finishYear = Integer.parseInt(secondValue);
            if (startlYear > finishYear) {
                logger.info("Incorrect year specified: initial year " + startlYear + " final year " + finishYear);
                return;
            }
            List<Book> foundBooks = clientServiceREST.searchBooksByYearRange(startlYear, finishYear, jwt);
            if (foundBooks.isEmpty()) {
                logger.info("No books found");
            } else {
                foundBooks.forEach(logger::info);
            }
        } catch (NumberFormatException e) {
            logger.info("Incorrect year specified: initial year " + firstValue + " final year " + secondValue);
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
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
            List<Book> foundBooks = clientServiceREST.searchBookByYearPagesNumberAndTitle(year, pagesNumber, bookTitle, jwt);
            if (foundBooks.isEmpty()) {
                logger.info("No books found");
            } else {
                logger.info(foundBooks);
            }
        } catch (NumberFormatException e) {
            logger.info("Incorrect input data");
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
        }
    }
    private void addBookmark(BufferedReader reader) throws IOException {
        System.out.println("Enter the book title");
        String bookTitle = reader.readLine();
        try {
            Book book = clientServiceREST.findBookByFullTitle(bookTitle, jwt);
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
            if (!clientServiceREST.addBookmark(bookTitle, pageNumber, jwt)) {
                logger.info("The bookmark already exists in this book");
                return;
            }
            logger.info("Bookmark was added to the " + bookTitle + " book on the page " + pageNumber);
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
        }
    }

    private void deleteBookmark(BufferedReader reader) throws IOException {
        try {
            System.out.println("Enter the book title");
            String bookTitle = reader.readLine();
            if (!clientServiceREST.deleteBookmark(bookTitle, jwt)) {
                logger.info("There is no bookmark in this book: " + bookTitle);
                return;
            }
            logger.info("Bookmark deleted - book title: " + bookTitle);
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
        }
    }

    private void showBooksWithBookmarks() {
        try {
            List<Bookmark> foundBookmarks = clientServiceREST.showBooksWithBookmarks(jwt);
            if (foundBookmarks.isEmpty()) {
                logger.info("There are no bookmarks");
            }
            foundBookmarks.forEach(logger::info);
        } catch (AccessDeniedException e) {
            logger.error((e.getMessage()));
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
        if (!role.equals("ADMINISTRATOR")) {
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
        if (!role.equals("ADMINISTRATOR")) {
            return;
        }
        try {
            System.out.println("Enter user login");
            String login = reader.readLine();
            System.out.println("Enter user password");
            String password = reader.readLine();
            if (!clientServiceREST.addUser(login, password, jwt)) {
                logger.info("This user already exists - " + login);
                return;
            }
            logger.info("New user added - " + login);
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
        }
    }

    private void blockUser(BufferedReader reader) throws IOException {
        if (!role.equals("ADMINISTRATOR")) {
            return;
        }
        try {
            System.out.println("Enter user login");
            String login = reader.readLine();
            if(!clientServiceREST.blockUser(login, jwt)) {
                logger.info("User does not exist - " + login);
                return;
            }
            logger.info("User deleted - " + login);
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
        }
    }

    private void showHistory() {
        if (!role.equals("ADMINISTRATOR")) {
            return;
        }
        try {
            List<String> fullHistory = clientServiceREST.showHistory(jwt);
            fullHistory.forEach(logger::info);
        } catch (AccessDeniedException e) {
            logger.error(e.getMessage());
        }
    }
}


