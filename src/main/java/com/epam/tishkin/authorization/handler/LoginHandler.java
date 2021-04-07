package com.epam.tishkin.authorization.handler;

import com.epam.tishkin.authorization.AccountsList;
import com.epam.tishkin.authorization.Account;
import com.epam.tishkin.authorization.exception.InvalidAutorizationException;
import com.epam.tishkin.client.Visitor;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class LoginHandler extends Handler {

    public LoginHandler(String login, String password) {
        Handler.login = login;
        Handler.password = password;
    }

    @Override
    public Visitor check() throws InvalidAutorizationException {
        AccountsList accountsList = parseFromJSONFile();
        Optional<Account> currentCustomer = accountsList.getAccounts()
                .stream()
                .filter(account -> login.equals(account.getLogin()))
                .findFirst();
        account = currentCustomer.orElseThrow(InvalidAutorizationException::new);
        next = new PasswordHandler();
        return next.check();
    }

    private AccountsList parseFromJSONFile() {
        AccountsList accountsList = null;
        try (FileReader reader = new FileReader("src/main/resources/accounts.json")) {
            Gson gson = new Gson();
            accountsList = gson.fromJson(reader, AccountsList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accountsList;
    }
}
