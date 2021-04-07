package com.epam.tishkin.authorization;

import java.util.ArrayList;
import java.util.List;

public class AccountsList {
   private List<Account> accounts;

    public List<Account> getAccounts() {
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        return this.accounts;
    }
}
