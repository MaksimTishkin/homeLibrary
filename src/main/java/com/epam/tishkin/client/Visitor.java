package com.epam.tishkin.client;

import com.epam.tishkin.library.LibraryAPI;

public abstract class Visitor {
    private final String name;
    private LibraryAPI libraryAPI;

    Visitor(String name) {
        this.name = name;
        libraryAPI = new LibraryAPI(this);
        libraryAPI.startLibraryUse();
    }

    public String getName() {
        return name;
    }
}
