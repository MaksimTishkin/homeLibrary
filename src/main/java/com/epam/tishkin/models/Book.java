package com.epam.tishkin.models;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table (name = "Book")
@XmlRootElement

public class Book implements Serializable {
    private static final long serialVersionUID = 965896523L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "Title")
    @XmlElement
    private String title;
    @Column(name = "ISBNumber")
    @XmlElement
    private String ISBNumber;
    @Column(name = "Publication_Year")
    @XmlElement
    private int publicationYear;
    @Column(name = "Pages_Number")
    @XmlElement
    private int pagesNumber;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Author_name")
    @XmlElement
    private Author author;

    public Book() {
    }

    public Book(String title, String ISBNumber, int year, int pagesNumber, Author author) {
        this.title = title;
        this.ISBNumber = ISBNumber;
        this.publicationYear = year;
        this.pagesNumber = pagesNumber;
        this.author = author;
    }

    public int getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getISBNumber() {
        return ISBNumber;
    }

    public void setISBNumber(String ISBNumber) {
        this.ISBNumber = ISBNumber;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public int getPagesNumber() {
        return pagesNumber;
    }

    public void setPagesNumber(int pagesNumber) {
        this.pagesNumber = pagesNumber;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Author: " + getAuthor() + " Title: " + title;
    }
}