package com.epam.tishkin.library;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Author> authors;

    public List<Author> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        return authors;
    }
}
