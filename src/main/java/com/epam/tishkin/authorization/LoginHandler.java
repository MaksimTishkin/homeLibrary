package com.epam.tishkin.authorization;

import com.epam.tishkin.authorization.exceptions.InvalidAutorizationException;
import com.epam.tishkin.client.Person;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class LoginHandler {
    private final PasswordHandler next;

    public LoginHandler() {
        this.next = new PasswordHandler();
    }

    public Person check(String login, String password) throws InvalidAutorizationException {
        Accounts accounts = parseFromJSONFile();
        Optional<Customer> currentCustomer = accounts.getAccounts()
                .stream()
                .filter(customer -> login.equals(customer.getLogin()))
                .findFirst();
        Customer validCustomer = currentCustomer.orElseThrow(InvalidAutorizationException::new);
        return next.check(password, validCustomer);
    }

    private Accounts parseFromJSONFile() {
        Accounts accounts = null;
        try (FileReader reader = new FileReader("src/main/resources/accounts.json")) {
            Gson gson = new Gson();
            accounts = gson.fromJson(reader, Accounts.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accounts;
    }
}
