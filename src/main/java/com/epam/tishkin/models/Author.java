package com.epam.tishkin.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table (name = "Author")
public class Author implements Serializable {
    private static final long serialVersionUID = 785478548L;

    @Id
    @JsonProperty("name")
    private String name;
    @OneToMany(mappedBy = "author", orphanRemoval = true)
    @JsonProperty("book")
    private List<Book> book;

    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBook() {
        return book;
    }

    public void setBook(List<Book> book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return name;
    }
}