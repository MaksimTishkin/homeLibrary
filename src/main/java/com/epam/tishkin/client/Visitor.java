package com.epam.tishkin.client;

import com.epam.tishkin.authorization.exception.AuthorDoesNotExistException;
import com.epam.tishkin.authorization.exception.BookDoesNotExistException;
import com.epam.tishkin.library.Author;
import com.epam.tishkin.library.Book;
import com.epam.tishkin.library.Library;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public abstract class Visitor {
    private static final Properties properties = new Properties();
    final static Logger logger = LogManager.getLogger(Visitor.class);
    Comparator<Author> authorComparator = Comparator.comparing(Author::getName);
    private final String name;
    private Library library;

    Visitor(String name) {
        this.name = name;
        getLibraryFromJSON();
        library.getAuthors().sort(authorComparator);
    }

    private void getLibraryFromJSON() {
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
    }

    public void startLibraryUse() {
        String request;
        String bookTitle;
        String bookAuthor;
        long ISBNumber;
        int year;
        int pagesNumber;
        Book currentBook;
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
                System.out.println("0 Settings (for administrators only)");
                System.out.println("15 Exit");
                request = reader.readLine();
                switch (request) {
                    case "1":
                        System.out.println("Enter the book title");
                        bookTitle = reader.readLine();
                        System.out.println("Enter the author of the book");
                        bookAuthor = reader.readLine();
                        System.out.println("Enter the book ISBN number");
                        try {
                            ISBNumber = Long.parseLong(reader.readLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect ISBN number" + "\n");
                            break;
                        }
                        System.out.println("Enter the year of publication");
                        try {
                            year = Integer.parseInt(reader.readLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect year of publication" + "\n");
                            break;
                        }
                        System.out.println("Enter the number of pages");
                        try {
                            pagesNumber = Integer.parseInt(reader.readLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect number of pages" + "\n");
                            break;
                        }
                        currentBook = new Book(bookTitle, bookAuthor, ISBNumber, year, pagesNumber);
                        if (library.addBook(currentBook)) {
                            System.out.println("Book has been added" + "\n");
                            writeToHistory(this.name + ": new book added " + currentBook);
                        } else {
                            System.out.println("Such a book already exists" + "\n");
                        }
                        break;
                    case "2":
                        System.out.println("Enter the book title you want to delete");
                        bookTitle = reader.readLine();
                        System.out.println("Enter the book author");
                        bookAuthor = reader.readLine();
                        if (library.deleteBook(bookTitle, bookAuthor)) {
                            System.out.println("The book was deleted" + "\n");
                            writeToHistory(this.name + ": book deleted " + bookTitle);
                        } else {
                            System.out.println("There is no such book in the library" + "\n");
                        }
                        break;
                    case "3":
                        System.out.println("Enter the author's name");
                        bookAuthor = reader.readLine();
                        if (library.addAuthor(bookAuthor)) {
                            System.out.println("The author was added" + "\n");
                            writeToHistory(this.name + ": new author added " + bookAuthor);
                        } else {
                            System.out.println("Such an author already exists" + "\n");
                        }
                        break;
                    case "4":
                        System.out.println("Enter the author's name");
                        bookAuthor = reader.readLine();
                        if (library.deleteAuthor(bookAuthor)) {
                            System.out.println("The author was deleted" + "\n");
                            writeToHistory(this.name + ": author deleted " + bookAuthor);
                        } else {
                            System.out.println("There is no such author" + "\n");
                        }
                        break;
                    case "5":
                        System.out.println("Specify the path to the folder");
                        String CSVfileName = reader.readLine();
                        System.out.println("Books added successfully: " + library.addBooksFromCSV(CSVfileName) + "\n");
                        writeToHistory(this.name + ": added books from the catalog");
                        break;
                    case "6":
                        System.out.println("Specify the path to the folder");
                        String JSONfileName = reader.readLine();
                        System.out.println("Books added successfully: " + library.addBooksFromJSON(JSONfileName) + "\n");
                        writeToHistory(this.name + ": added books from the catalog");
                        break;
                    case "7":
                        System.out.println("Enter part of the book title");
                        bookTitle = reader.readLine();
                        try {
                            currentBook = library.searchBookForTitle(bookTitle);
                            System.out.println("Book found: " + currentBook + "\n");
                            writeToHistory(this.name + ": book found " + currentBook);
                        } catch (BookDoesNotExistException e) {
                            System.out.println(e.getMessage() + "\n");
                        }
                        break;
                    case "8":
                        System.out.println("Enter part of the book title");
                        bookTitle = reader.readLine();
                        System.out.println("Enter the page number");
                        try {
                            pagesNumber = Integer.parseInt(reader.readLine());
                            currentBook = library.addBookmark(bookTitle, pagesNumber);
                            System.out.println("Bookmark added: " + currentBook + "\n");
                            writeToHistory(this.name + ": bookmark added on page " + pagesNumber);
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect number of page" + "\n");
                        } catch (BookDoesNotExistException e) {
                            System.out.println(e.getMessage() + "\n");
                        }
                        break;
                    case "9":
                        System.out.println("Enter part of the book title");
                        bookTitle = reader.readLine();
                        try {
                            currentBook = library.deleteBookmark(bookTitle);
                            System.out.println("Bookmark deleted: " + currentBook + "\n");
                            writeToHistory(this.name + ": bookmark deleted");
                        } catch (BookDoesNotExistException e) {
                            System.out.println(e.getMessage() + "\n");
                        }
                        break;
                    case "10":
                        System.out.println("Enter part of the author's name");
                        bookAuthor = reader.readLine();
                        try {
                            Author author = library.searchBooksForAuthor(bookAuthor);
                            author.getBooks().forEach(System.out::println);
                            writeToHistory(this.name + ": find books by author " + author);
                        } catch (AuthorDoesNotExistException e) {
                            System.out.println(e.getMessage() + "\n");
                        }
                        break;
                    case "11":
                        System.out.println("Enter book's ISBN number");
                        String number = reader.readLine();
                        if (number.length() != 13) {
                            System.out.println("Incorrect number" + "\n");
                            break;
                        }
                        try {
                            ISBNumber = Long.parseLong(number);
                            currentBook= library.searchBookForISBN(ISBNumber);
                            System.out.println("Book found: " + currentBook + "\n");
                            writeToHistory(this.name + ": find book by ISBN");
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect number" + "\n");
                        } catch (BookDoesNotExistException e) {
                            System.out.println(e.getMessage() + "\n");
                        }
                        break;
                    case "12":
                        System.out.println("Enter the initial year value");
                        String firstValue = reader.readLine();
                        System.out.println("Enter the final year value");
                        String secondValue = reader.readLine();
                        try {
                            int initialYear = Integer.parseInt(firstValue);
                            int finalYear = Integer.parseInt(secondValue);
                            List<Book> foundBooks = library.searchBooksByYearRange(initialYear, finalYear);
                            foundBooks.forEach(System.out::println);
                            writeToHistory(this.name + ": find books by year range " + initialYear + "-" + finalYear);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid year specified" + "\n");
                        }
                        break;
                    case "13":
                        try {
                            System.out.println("Enter the year value");
                            year = Integer.parseInt(reader.readLine());
                            System.out.println("Enter the pages number");
                            pagesNumber= Integer.parseInt(reader.readLine());
                            System.out.println("Enter part of the book title");
                            bookTitle = reader.readLine();
                            currentBook = library.searchBookByYearPagesNumberAndTitle(year, pagesNumber, bookTitle);
                            System.out.println("Book found: " + currentBook + "\n");
                            writeToHistory(this.name + ": find book " + currentBook);
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect input data" + "\n");
                        } catch (BookDoesNotExistException e) {
                            System.out.println(e.getMessage() + "\n");
                        }
                        break;
                    case "14":
                        List<Book> books = library.searchBooksWithBookmark();
                        books.forEach(System.out::println);
                        writeToHistory(this.name + ": find books with bookmark");
                        break;
                    case "0":
                        if (this.getClass() == Administrator.class) {
                            Administrator administrator = (Administrator) this;
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

    private void writeLibraryToJSON() {
        try (FileWriter writer = new FileWriter(properties.getProperty("pathFromLibrary"))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(library));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static void writeToHistory(String message) {
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
