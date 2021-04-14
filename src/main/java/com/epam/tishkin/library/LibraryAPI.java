package com.epam.tishkin.library;

import com.epam.tishkin.authorization.exception.AuthorDoesNotExistException;
import com.epam.tishkin.authorization.exception.BookDoesNotExistException;
import com.epam.tishkin.client.Administrator;
import com.epam.tishkin.client.Visitor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class LibraryAPI {
    private static final Properties properties = new Properties();
    final static Logger logger = LogManager.getLogger(LibraryAPI.class);
    private final Library library;
    private final Visitor visitor;

    public LibraryAPI(Visitor visitor) {
        this.visitor = visitor;
        library = getLibraryFromJSON();
        library.getAuthors().sort(Comparator.comparing(Author::getName));
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
                    case "16":
                        if (visitor instanceof Administrator) {
                            Administrator administrator = (Administrator) visitor;
                            String login;
                            System.out.println("1 Add a new user");
                            System.out.println("2 Block user");
                            System.out.println("3 Show history");
                            request = reader.readLine();
                            switch (request) {
                                case "1":
                                    System.out.println("Enter login");
                                    login = reader.readLine();
                                    System.out.println("Enter password");
                                    String password = reader.readLine();
                                    administrator.addNewUser(login, password);
                                    break;
                                case "2":
                                    System.out.println("Enter login");
                                    login = reader.readLine();
                                    administrator.blockUser(login);
                                    break;
                                case "3":
                                    administrator.showHistory();
                                    break;
                            }
                        } else {
                            System.out.println("You are not an administrator" + "\n");
                        }
                        break;
                    case "15":
                        writeLibraryToJSON();
                        return;
                }
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void addNewBook(BufferedReader reader) throws IOException {
        int publicationYear;
        long ISBNumber;
        int pagesNumber;
        Book currentBook;
        System.out.println("Enter the book title");
        String bookTitle = reader.readLine();
        System.out.println("Enter the author of the book");
        String bookAuthor = reader.readLine();
        System.out.println("Enter the book ISBN number");
        String individualNumber = reader.readLine();
        if (individualNumber.length() != 13) {
            logger.info("Incorrect ISBNumber: " + individualNumber);
            return;
        }
        try {
            ISBNumber = Long.parseLong(individualNumber);
        } catch (NumberFormatException e) {
            logger.info("Incorrect ISBNumber: " + individualNumber);
            return;
        }
        System.out.println("Enter the year of publication");
        String year = reader.readLine();
        try {
            publicationYear = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            logger.info("Incorrect year of publication: " + year);
            return;
        }
        System.out.println("Enter the number of pages");
        String number = reader.readLine();
        try {
            pagesNumber = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            logger.info("Incorrect year of publication: " + number);
            return;
        }
        currentBook = new Book(bookTitle, bookAuthor, ISBNumber, publicationYear, pagesNumber);
        if (library.addBook(currentBook)) {
            logger.info("Book has been added: " + currentBook);
            writeToHistory(visitor.getName() + ": new book added " + currentBook);
        } else {
            logger.info("Such a book already exists: " + currentBook);
        }
    }


    private void deleteBook(BufferedReader reader) throws IOException {
        System.out.println("Enter the book title you want to delete");
        String bookTitle = reader.readLine();
        System.out.println("Enter the book author");
        String bookAuthor = reader.readLine();
        if (library.deleteBook(bookTitle, bookAuthor)) {
            logger.info("The book was deleted: " + bookTitle + "author: " + bookAuthor);
            writeToHistory(visitor.getName() + ": book deleted " + bookTitle + "author: " + bookAuthor);
        } else {
            logger.info("There is no such book in the library" + bookTitle + "author: " + bookAuthor);
        }
    }

    private void addAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter the author's name");
        String bookAuthor = reader.readLine();
        if (library.addAuthor(bookAuthor)) {
            logger.info("The author was added: " + bookAuthor);
               writeToHistory(visitor.getName() + ": new author added " + bookAuthor);
        } else {
            logger.info("Such an author already exists: " + bookAuthor);
        }
    }

    private void deleteAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter the author's name");
        String bookAuthor = reader.readLine();
        if (library.deleteAuthor(bookAuthor)) {
            logger.info("The author was deleted: " + bookAuthor);
            writeToHistory(visitor.getName() + ": author deleted: " + bookAuthor);
        } else {
            logger.info("There is no such author: " + bookAuthor);
        }
    }

    private void addBooksFromCSVcatalog(BufferedReader reader) throws IOException {
        System.out.println("Enter the path to the folder");
        String fileName = reader.readLine();
        int booksAdded = library.addBooksFromCSV(fileName);
        logger.info("Books added successfully from CSV: " + booksAdded);
        writeToHistory(visitor.getName() + ": number of books added from CSV catalog: " + booksAdded);
    }

    private void addBooksFromJSONcatalog(BufferedReader reader) throws IOException {
        System.out.println("Enter the path to the folder");
        String fileName = reader.readLine();
        long booksAdded = library.addBooksFromJSON(fileName);
        logger.info("Books added successfully from JSON: " + booksAdded);
        writeToHistory(visitor.getName() + ": number of books added from JSON catalog: " + booksAdded);
    }

    private void searchBookForTitle(BufferedReader reader) throws IOException {
        System.out.println("Enter part of the book title");
        String bookTitle = reader.readLine();
        try {
            Book currentBook = library.searchBookForTitle(bookTitle);
            logger.info("Book found: " + currentBook.getTitle() + "author: " + currentBook.getAuthor());
            writeToHistory(visitor.getName() + ": book found " + currentBook + "author: " + currentBook.getAuthor());
        } catch (BookDoesNotExistException e) {
            logger.info(e.getMessage());
        }
    }

    private void searchBooksForAuthor(BufferedReader reader) throws IOException {
        System.out.println("Enter part of the author's name");
        String bookAuthor = reader.readLine();
        try { Author author = library.searchBooksForAuthor(bookAuthor);author.getBooks().forEach(System.out::println);
            logger.info("Find books by author: " + author);
            writeToHistory(visitor.getName() + ": find books by author " + author);
        } catch (AuthorDoesNotExistException e) {
            logger.info(e.getMessage());
        }
    }

    private void searchBookForISBNumber(BufferedReader reader) throws IOException {
        System.out.println("Enter book's ISBN number");
        String number = reader.readLine();
        if (number.length() != 13) {
            logger.info("Incorrect ISBN number: " + number);
            return;
        }
        try {
            long ISBNumber = Long.parseLong(number);
            Book currentBook = library.searchBookForISBN(ISBNumber);
            logger.info("Book found: " + currentBook.getTitle() + "by ISBN: " + number);
            writeToHistory(visitor.getName() + " Book found: " + currentBook.getTitle() + "by ISBN: " + number);
        } catch (NumberFormatException e) {
            logger.info("Incorrect ISBN number: " + number);
        } catch (BookDoesNotExistException e) {
            logger.info(e.getMessage());
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
            List<Book> foundBooks = library.searchBooksByYearRange(initialYear, finalYear);
            foundBooks.forEach(System.out::println);
            logger.info("Find books by year range: " + initialYear + " - " + finalYear);
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
            Book currentBook = library.searchBookByYearPagesNumberAndTitle(year, pagesNumber, bookTitle);
            logger.info("Book found by year, pages number and title: " + currentBook
                    + "year: " + currentBook.getYear() + " pages number: " + currentBook.getPagesNumber());
            writeToHistory(visitor.getName() + ": find book " + currentBook);
        } catch (NumberFormatException e) {
            logger.info("Incorrect input data");
        } catch (BookDoesNotExistException e) {
            logger.info(e.getMessage());
        }
    }

    private void addBookmark(BufferedReader reader) throws IOException {
        System.out.println("Enter the book title");
        String bookTitle = reader.readLine();
        System.out.println("Enter the page number");
        int pageNumber = Integer.parseInt(reader.readLine());
        visitor.addBookmark(bookTitle, pageNumber);
        logger.info("Bookmark added - book title: " + bookTitle + " page: " + pageNumber);
        writeToHistory(visitor.getName() + "Bookmark added - book title: " + bookTitle + " page: " + pageNumber);
        }

    private void deleteBookmark(BufferedReader reader) throws IOException {
        System.out.println("Enter the book title");
        String bookTitle = reader.readLine();
        if (visitor.deleteBookmark(bookTitle)) {
            logger.info("Bookmark deleted - book title: " + bookTitle);
            writeToHistory(visitor.getName() + "Bookmark deleted - book title: " + bookTitle);
        } else {
            logger.info("There is no bookmark in this book: " + bookTitle);
        }
    }

    private void showBooksWithBookmarks() {
        visitor.getMyBookmarks().forEach(System.out::println);
        logger.info("Show books with visitor's bookmark");
        writeToHistory(visitor.getName() + ": Show books with visitor's bookmark");
    }

    private static Library getLibraryFromJSON() {
        Library library = null;
        try (FileReader readerForProperties = new FileReader("src/main/resources/config.properties")) {
            properties.load(readerForProperties);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        try (FileReader reader = new FileReader(properties.getProperty("pathFromLibrary"))) {
            Gson gson = new Gson();
            library = gson.fromJson(reader, Library.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return library;
    }

    private void writeLibraryToJSON () {
        try (FileWriter writer = new FileWriter(properties.getProperty("pathFromLibrary"))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(library));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
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
}
