package com.epam.tishkin.models;

import java.util.ArrayList;
import java.util.List;

public class AuthorsList {
    private List<Author> authors;

    public List<Author> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        return authors;
    }
}
