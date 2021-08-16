package com.epam.tishkin.models;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table (name = "Author")
@XmlRootElement
public class Author implements Serializable {
    private static final long serialVersionUID = 785478548L;
    @Id
    @XmlElement
    private String name;

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