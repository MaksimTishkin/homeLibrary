/**
package com.epam.tishkin.authorization.handler;

import com.epam.tishkin.authorization.AccountsList;
import com.epam.tishkin.authorization.Account;
import com.epam.tishkin.exception.InvalidAutorizationException;
import com.epam.tishkin.client.Visitor;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class LoginHandler extends Handler {
    private static final Properties properties = new Properties();
    final static Logger logger = LogManager.getLogger(LoginHandler.class);

    public LoginHandler(String login, String password) {
        Handler.login = login;
        Handler.password = password;
    }

    @Override
    public Visitor check() throws InvalidAutorizationException {
        AccountsList accountsList = parseJSONFile();
        Optional<Account> currentCustomer = accountsList.getAccounts()
                .stream()
                .filter(account -> login.equals(account.getLogin()))
                .findFirst();
        account = currentCustomer.orElseThrow(() -> new InvalidAutorizationException("Invalid login/password"));
        next = new PasswordHandler();
        return next.check();
    }

    private AccountsList parseJSONFile() {
        AccountsList accounts = null;
        try (FileReader readerForProperties = new FileReader("src/main/resources/config.properties")) {
            properties.load(readerForProperties);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        try (FileReader reader = new FileReader(properties.getProperty("pathFromAccounts"))) {
            Gson gson = new Gson();
            accounts = gson.fromJson(reader, AccountsList.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return accounts;
    }
}
 */
