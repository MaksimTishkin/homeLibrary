package com.epam.tishkin.authorization;

public class Account {
    private final String login;
    private final String password;
    private boolean isAdmin;

    public Account(String login, String password) {
        this.login = login;
        this.password = password;
        isAdmin = false;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean status) {
        isAdmin = status;
    }
}
