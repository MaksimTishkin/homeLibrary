package com.epam.tishkin.client;

import com.epam.tishkin.library.Book;
import com.epam.tishkin.library.Library;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class Visitor {
    private String name;
    private Library library;

    Visitor(String name) {
        this.name = name;
        getLibraryFromJSON();
    }

    public void getLibraryFromJSON() {
        try (FileReader reader = new FileReader("src/main/resources/library.json")) {
            Gson gson = new Gson();
            library = gson.fromJson(reader, Library.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startLibraryUse() {
        String request = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while(true) {
                System.out.println("1 Add a new book");
                System.out.println("10 Exit");
                request = reader.readLine();
                switch (request) {
                    case "1":
                        System.out.println("Enter the title, author, ISBN number and year of publication");
                        String[] bookInformation = reader.readLine().split(" ");
                        String title = bookInformation[0];
                        String author = bookInformation[1];
                        long ISBNNumber;
                        int year;
                        try {
                            ISBNNumber = Long.parseLong(bookInformation[2]);
                            year =   Integer.parseInt(bookInformation[3]);
                        } catch (NumberFormatException e) {
                            System.out.println("Number and/or year of publication incorrect");
                            break;
                        }
                        library.addBook(new Book(title, author, ISBNNumber, year));
                        System.out.println("Book has been added");
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
