package com.epam.tishkin.models;

import jakarta.xml.bind.annotation.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Bookmark")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement()
public class Bookmark implements Serializable {
    private final static long serialVersionUID = 98745874L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    private int id;
    @Column(name = "Book_title")
    @XmlElement()
    private String title;
    @Column(name = "Page_number")
    @XmlElement()
    private int page;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_login")
    private User user;

    public Bookmark() {

    }

    public Bookmark(String title, int page, User user) {
        this.title = title;
        this.page = page;
        this.user = user;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Book title: " + title + ", page with bookmark: " + page;
    }
}
