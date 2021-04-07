package com.epam.tishkin.client;

import com.epam.tishkin.library.Library;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;

public abstract class Visitor {
    String name;

    Visitor(String name) {
        this.name = name;
    }

    public Library getLibraryFromJSON() {
        Library myLibrary = null;
        try (FileReader reader = new FileReader("src/main/resources/library.json")) {
            Gson gson = new Gson();
            myLibrary = gson.fromJson(reader, Library.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myLibrary;
    }
}
