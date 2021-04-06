package com.epam.tishkin.authorization;

public class Customer {
    private String login;
    private String password;
    private boolean isAdmin;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    @Override
    public String toString() {
        return login + " " + password + " " + isAdmin;
    }
}
