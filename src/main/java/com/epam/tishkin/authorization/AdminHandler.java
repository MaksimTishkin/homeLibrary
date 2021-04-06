package com.epam.tishkin.authorization;

import com.epam.tishkin.client.Administrator;
import com.epam.tishkin.client.Person;
import com.epam.tishkin.client.User;

public class AdminHandler {

    public Person check(Customer customer) {
        if (customer.getIsAdmin()) {
            return new Administrator(customer.getLogin());
        }
        return new User(customer.getLogin());
    }
}
