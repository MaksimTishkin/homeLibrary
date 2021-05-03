package com.epam.tishkin.authorization.handler;

import com.epam.tishkin.client.Administrator;
import com.epam.tishkin.client.Visitor;
import com.epam.tishkin.client.User;

public class AdminHandler extends Handler {

    @Override
    public Visitor check() {
        if (account.getIsAdmin()) {
            return new Administrator(account.getLogin());
        }
        return new User(account.getLogin());
    }
}
