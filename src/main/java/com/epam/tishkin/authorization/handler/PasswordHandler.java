package com.epam.tishkin.authorization.handler;

import com.epam.tishkin.authorization.exception.InvalidAutorizationException;
import com.epam.tishkin.client.Visitor;

public class PasswordHandler extends Handler {

    @Override
    public Visitor check() throws InvalidAutorizationException {
        if (password.equals(account.getPassword())) {
            next = new AdminHandler();
            return next.check();
        } else {
            throw new InvalidAutorizationException();
        }
    }
}