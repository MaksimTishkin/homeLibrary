package com.epam.tishkin.client;

import com.epam.tishkin.library.Library;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;

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

    public Library getLibrary() {
        return library;
    }
}
