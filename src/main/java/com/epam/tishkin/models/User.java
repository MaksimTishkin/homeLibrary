package com.epam.tishkin.models;

import jakarta.xml.bind.annotation.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "User")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement()
public class User implements Serializable {
    private final static long serialVersionUID = 65896589L;
    @Id
    @Column (name = "Login")
    @XmlElement()
    private String login;
    @Column (name = "Password")
    @XmlElement()
    private String password;
    @Column (name = "Role")
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @XmlElementWrapper(name = "bookmarks")
    @XmlElement(name = "bookmark", type = Bookmark.class)
    //@XmlTransient
    private List<Bookmark> bookmarks;

    public User() {

    }

    public User(String login, String password, Role role) {
        this.login = login;
        this.password = password;
        this.role = role;
        bookmarks = new ArrayList<>();
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public void addBookmark(Bookmark bookmark) {
        bookmark.setUser(this);
        bookmarks.add(bookmark);
    }

    public void removeBookmark(Bookmark bookmark) {
        bookmarks.remove(bookmark);
    }
}
