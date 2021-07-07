package com.epam.tishkin.models;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table (name = "Author")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement()
public class Author implements Serializable {
    private static final long serialVersionUID = 785478548L;

    @Id
    @XmlElement()
    private String name;
    //@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<Book> books;

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

    @Override
    public String toString() {
        return name;
    }
}
