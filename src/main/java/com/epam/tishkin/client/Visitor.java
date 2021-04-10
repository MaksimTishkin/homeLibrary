package com.epam.tishkin.client;

import com.epam.tishkin.library.Author;
import com.epam.tishkin.library.Book;
import com.epam.tishkin.library.Library;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while(true) {
                System.out.println("1 Add a new book");
                System.out.println("2 Delete book");
                System.out.println("3 Add a new author");
                System.out.println("4 Delete author");
                System.out.println("10 Exit");
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
                        if (library.addBook(bookTitle, bookAuthor, ISBNumber, year, pagesNumber)) {
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
                    case "10":
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
