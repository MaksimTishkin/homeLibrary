package com.epam.tishkin.client;

import com.epam.tishkin.authorization.exception.AuthorDoesNotExistException;
import com.epam.tishkin.authorization.exception.BookDoesNotExistException;
import com.epam.tishkin.library.Author;
import com.epam.tishkin.library.Book;
import com.epam.tishkin.library.Library;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;

public abstract class Visitor {
    private String name;
    private Library library;

    Visitor(String name) {
        this.name = name;
        getLibraryFromJSON();
        library.getAuthors().sort(Comparator.comparing(Author::getName));
    }

    private void getLibraryFromJSON() {
        try (FileReader reader = new FileReader("src/main/resources/library.json")) {
            Gson gson = new Gson();
            library = gson.fromJson(reader, Library.class);
        } catch (IOException e) {
            e.printStackTrace();
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
                        } else {
                            System.out.println("There is no such book in the library" + "\n");
                        }
                        break;
                    case "3":
                        System.out.println("Enter the author's name");
                        bookAuthor = reader.readLine();
                        if (library.addAuthor(bookAuthor)) {
                            System.out.println("The author was added" + "\n");
                        } else {
                            System.out.println("Such an author already exists" + "\n");
                        }
                        break;
                    case "4":
                        System.out.println("Enter the author's name");
                        bookAuthor = reader.readLine();
                        if (library.deleteAuthor(bookAuthor)) {
                            System.out.println("The author was deleted" + "\n");
                        } else {
                            System.out.println("There is no such author" + "\n");
                        }
                        break;
                    case "5":
                        System.out.println("Specify the path to the folder");
                        String CSVfileName = reader.readLine();
                        System.out.println("Books added successfully: " + library.addBooksFromCSV(CSVfileName) + "\n");
                        break;
                    case "6":
                        System.out.println("Specify the path to the folder");
                        String JSONfileName = reader.readLine();
                        System.out.println("Books added successfully: " + library.addBooksFromJSON(JSONfileName) + "\n");
                        break;
                    case "7":
                        System.out.println("Enter part of the book title");
                        bookTitle = reader.readLine();
                        try {
                            currentBook = library.searchBookForTitle(bookTitle);
                            System.out.println("Book found: " + currentBook + "\n");
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
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect number of page" + "\n");
                            break;
                        }
                        try {
                            library.addBookmark(bookTitle, pagesNumber);
                        } catch (BookDoesNotExistException e) {
                            System.out.println("e.getMessage()" + "\n");
                        }
                        break;
                    case "9":
                        System.out.println("Enter part of the book title");
                        bookTitle = reader.readLine();
                        try {
                            library.deleteBookmark(bookTitle);
                        } catch (BookDoesNotExistException e) {
                            System.out.println("e.getMessage()" + "\n");
                        }
                        break;
                    case "10":
                        System.out.println("Enter part of the author's name");
                        bookAuthor = reader.readLine();
                        try {
                            Author author = library.searchBooksForAuthor(bookAuthor);
                            author.getBooks().forEach(System.out::println);
                        } catch (AuthorDoesNotExistException e) {
                            System.out.println(e.getMessage() + "\n");
                        }
                        break;
                    case "15":
                        return;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Library getLibrary() {
        return library;
    }
}
