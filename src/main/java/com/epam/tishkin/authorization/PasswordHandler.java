package com.epam.tishkin.authorization;

import com.epam.tishkin.authorization.exceptions.InvalidAutorizationException;
import com.epam.tishkin.client.Person;

public class PasswordHandler {
    private final AdminHandler next;

    public PasswordHandler() {
        next = new AdminHandler();
    }

    public Person check(String password, Customer customer) throws InvalidAutorizationException {
        if (password.equals(customer.getPassword())) {
            return next.check(customer);
        } else {
            throw new InvalidAutorizationException();
        }
    }
}
