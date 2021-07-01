package com.epam.tishkin.models;

import jakarta.xml.bind.annotation.*;

import javax.persistence.*;
import java.io.Serializable;

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

    public User() {

    }

    public User(String login, String password, Role role) {
        this.login = login;
        this.password = password;
        this.role = role;
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
}
