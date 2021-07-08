package com.epam.tishkin.models;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table (name = "Author")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement()
public class Author implements Serializable {
    private static final long serialVersionUID = 785478548L;

    @Id
    @XmlElement()
    private String name;
    @OneToMany(mappedBy = "author", orphanRemoval = true)
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
