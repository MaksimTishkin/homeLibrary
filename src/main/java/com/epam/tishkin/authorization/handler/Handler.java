package com.epam.tishkin.authorization.handler;

import com.epam.tishkin.authorization.Account;
import com.epam.tishkin.authorization.exception.InvalidAutorizationException;
import com.epam.tishkin.client.Visitor;

public abstract class Handler {
    protected Handler next;
    protected static Account account;
    protected static String login;
    protected static String password;

    public abstract Visitor check() throws InvalidAutorizationException;
}
