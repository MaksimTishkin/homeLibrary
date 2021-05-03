
package com.epam.tishkin.library;

import com.epam.tishkin.dao.LibraryDAO;
import com.epam.tishkin.client.Administrator;
import com.epam.tishkin.client.Visitor;
import com.epam.tishkin.models.Book;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.time.Year;
import java.util.Properties;

public class LibraryAPI {
    private static final Properties properties = new Properties();
    final static Logger logger = LogManager.getLogger(LibraryAPI.class);
    private final LibraryDAO libraryDAO;
    private final Visitor visitor;

    public LibraryAPI(Visitor visitor) {
        this.visitor = visitor;
        libraryDAO = new LibraryDAO();
    }

    public void startLibraryUse() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while(true) {
                System.out.println("1 Add a new book");
                System.out.println("2 Delete book");
                System.out.println("3 Add a new author");
                System.out.println("4 Delete author");
                System.out.println("5 Add books from CSV catalog");
                System.out.println("6 Add books from JSON catalog");
                System.out.println("7 Find a book by title");
                System.out.println("8 Add a bookmark to a book");
                System.out.println("9 Delete a bookmark from a book");
                System.out.println("10 Find books by author");
                System.out.println("11 Find book by ISBN number");
                System.out.println("12 Find books by year range");
                System.out.println("13 Find a book by year, number of pages, and title");
                System.out.println("14 Find books with my bookmark");
                System.out.println("15 Exit");
                if (visitor instanceof Administrator) {
                    System.out.println("16 Settings (for administrators only)");
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
                        addBooksFromCSVcatalog(reader);
                        break;
                    case "6":
                        addBooksFromJSONcatalog(reader);
                        break;
                    case "7":
                        searchBookForTitle(reader);
                        break;
                    case "8":
                        addBookmark(reader);
                        break;
                    case "9":
                        deleteBookmark(reader);
                        break;
                    case "10":
                        searchBooksForAuthor(reader);
                        break;
                    case "11":
                        searchBookForISBNumber(reader);
                        break;
                    case "12":
                        searchBooksForYearRange(reader);
                        break;
                    case "13":
                        searchBookByYearPagesNumberAndTitle(reader);
                        break;
                    case "14":
                        showBooksWithBookmarks();
                        break;
                    case "15":
                        libraryDAO.getConnector().closeSessionFactory();
                        return;
                    case "16":
                        if (visitor instanceof Administrator) {
                            useAdditionalAdministratorFeatures(reader);
                        }
                        break;
                }
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void addNewBook(BufferedReader reader) throws IOException {
        int publicationYear;
        String ISBNumber;
        int pagesNumber;
        Book currentBook;
        System.out.println("Enter the book title");
        String bookTitle = reader.readLine();
        System.out.println("Enter the author of the book");
        String bookAuthor = reader.readLine();
        System.out.println("Enter the book ISBN number");
        ISBNumber = reader.readLine();
        if (ISBNumber.length() != 13) {
            logger.info("Incorrect ISBNumber: " + ISBNumber);
            return;
        }
        try {
            Long.parseLong(ISBNumber);
        } catch (NumberFormatException e) {
            logger.info("Incorrect ISBNumber: " + ISBNumber);
            return;
        }
        System.out.println("Enter the year of publication");
        String year = reader.readLine();
        try {
            publicationYear = Integer.parseInt(year);
            int currentYear = Year.now().getValue();
            if (publicationYear > currentYear) {
                logger.info("Incorrect year of publication: " + year);
                return;
            }
        } catch (NumberFormatException e) {
            logger.info("Incorrect year of publication: " + year);
            return;
        }
        System.out.println("Enter the number of pages");
        String number = reader.readLine();
        try {
            pagesNumber = Integer.parseInt(number);
            if (pagesNumber <= 0) {
                logger.info("Invalid page value: " + number);
                return;
            }
        } catch (NumberFormatException e) {
            logger.info("Invalid page value: " + number);
            return;
        }
        currentBook = new Book(bookTitle, ISBNumber, publicationYear, pagesNumber);
        if (libraryDAO.addBook(currentBook, bookAuthor)) {
            writeToHistory(visitor.getName() + ": new book added " + currentBook);
        }
    }

    private void deleteBook(BufferedReader reader) throws IOException {
        System.out.println("Enter the book title you want to delete");
        String bookTitle = reader.readLine();
        System.out.println("Enter the book author");
        String bookAuthor = reader.readLine();
        if (libraryDAO.deleteBook(bookTitle, bookAuthor)) {
            writeToHistory(visitor.getName() + ": book deleted " + bookTitle + "author: " + bookAuthor);
        }
    }

    private void addAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter the author's name");
        String bookAuthor = reader.readLine();
        if (libraryDAO.addAuthor(bookAuthor)) {
            writeToHistory(visitor.getName() + ": new author added " + bookAuthor);
        }
    }

    private void deleteAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter the author's name");
        String bookAuthor = reader.readLine();
        if (libraryDAO.deleteAuthor(bookAuthor)) {
            writeToHistory(visitor.getName() + ": author deleted: " + bookAuthor);
        }
    }

    private void addBooksFromCSVcatalog(BufferedReader reader) throws IOException {
        System.out.println("Enter the path to the folder");
        String fileName = reader.readLine();
        int booksAdded = libraryDAO.addBooksFromCSV(fileName);
        writeToHistory(visitor.getName() + ": number of books added from CSV catalog: " + booksAdded);
    }

    private void addBooksFromJSONcatalog(BufferedReader reader) throws IOException {
        System.out.println("Enter the path to the folder");
        String fileName = reader.readLine();
        long booksAdded = libraryDAO.addBooksFromJSON(fileName);
        writeToHistory(visitor.getName() + ": number of books added from JSON catalog: " + booksAdded);
    }

    private void searchBookForTitle(BufferedReader reader) throws IOException {
        System.out.println("Enter part of the book title");
        String bookTitle = reader.readLine();
        libraryDAO.searchBookForTitle(bookTitle);
        writeToHistory(visitor.getName() + ": search for books by title: " + bookTitle);
        }

    private void searchBooksForAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter part of the author's name");
        String bookAuthor = reader.readLine();
        libraryDAO.searchBooksForAuthor(bookAuthor);
        writeToHistory(visitor.getName() + ": find books by author: " + bookAuthor);
    }

    private void searchBookForISBNumber(BufferedReader reader) throws IOException {
        System.out.println("Enter book's ISBN number");
        String number = reader.readLine();
        if (number.length() != 13) {
            logger.info("Incorrect ISBN number: " + number);
            return;
        }
        try {
            Long.parseLong(number);
        } catch (NumberFormatException e) {
            logger.info("Incorrect ISBN number: " + number);
            return;
        }
        if (libraryDAO.searchBookForISBN(number)) {
            writeToHistory(visitor.getName() + ": Book found by ISBN: " + number);
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
            libraryDAO.searchBooksByYearRange(initialYear, finalYear);
            writeToHistory(visitor.getName() + ": find books by year range " + initialYear + " - " + finalYear);
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
            libraryDAO.searchBookByYearPagesNumberAndTitle(year, pagesNumber, bookTitle);
            writeToHistory(visitor.getName() + ": find books by year, pages and title ");
        } catch (NumberFormatException e) {
            logger.info("Incorrect input data");
        }
    }

    private void addBookmark(BufferedReader reader) throws IOException {
        String page = "";
        System.out.println("Enter the book title");
        String bookTitle = reader.readLine();
        Book book = libraryDAO.findBookByFullTitle(bookTitle);
        if (book == null) {
            logger.info(bookTitle + " book not found");
            return;
        }
        System.out.println("Enter the page number");
        try {
            page = reader.readLine();
            int pageNumber = Integer.parseInt(page);
            if (pageNumber <= 0 || pageNumber > book.getPagesNumber()) {
                logger.info(pageNumber + " Invalid page value");
                return;
            }
            visitor.addBookmark(bookTitle, pageNumber);
            logger.info("Bookmark added - book title: " + bookTitle + " page: " + pageNumber);
            writeToHistory(visitor.getName() + ": Bookmark added - book title: " + bookTitle + " page: " + pageNumber);
        } catch (NumberFormatException e) {
            logger.info(page + " Invalid page value");
        }
    }

    private void deleteBookmark(BufferedReader reader) throws IOException {
        System.out.println("Enter the book title");
        String bookTitle = reader.readLine();
        if (visitor.deleteBookmark(bookTitle)) {
            logger.info("Bookmark deleted - book title: " + bookTitle);
            writeToHistory(visitor.getName() + ": Bookmark deleted - book title: " + bookTitle);
        } else {
            logger.info("There is no bookmark in this book: " + bookTitle);
        }
    }

    private void showBooksWithBookmarks() {
        if (visitor.getMyBookmarks().isEmpty()) {
            logger.info("No books with bookmarks");
        } else {
            visitor.getMyBookmarks().forEach(b -> logger.info("Book with bookmark - " + b.getTitle()
            + " on page " + b.getPage()));
        }
        writeToHistory(visitor.getName() + ": Show books with visitor's bookmark");
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

    private void useAdditionalAdministratorFeatures(BufferedReader reader) throws IOException {
        System.out.println("1 Add a new user");
        System.out.println("2 Block user");
        System.out.println("3 Show history");
        System.out.println("4 Go back to the previous menu");
        String request = reader.readLine();
        switch (request) {
            case "1":
                addNewUser(reader);
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

    private void addNewUser(BufferedReader reader) throws IOException {
        Administrator administrator = (Administrator) visitor;
        System.out.println("Enter login");
        String login = reader.readLine();
        System.out.println("Enter password");
        String password = reader.readLine();
        administrator.addNewUser(login, password);
        logger.info(administrator.getName() + " added new user - " + login + "with password - " + password);
    }

    private void blockUser(BufferedReader reader) throws IOException {
        Administrator administrator = (Administrator) visitor;
        System.out.println("Enter login");
        String login = reader.readLine();
        administrator.blockUser(login);
        logger.info(administrator.getName() + " Deleted the user - " + login);
    }

    private void showHistory() {
        Administrator administrator = (Administrator) visitor;
        administrator.showHistory();
        logger.info(administrator.getName() + " the history of actions is shown");
    }
}