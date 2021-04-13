package com.epam.tishkin.authorization;

import com.epam.tishkin.authorization.handler.LoginHandler;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AccountsList {
   private List<Account> accounts;

    public List<Account> getAccounts() {
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        return this.accounts;
    }
}
