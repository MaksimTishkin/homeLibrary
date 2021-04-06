package com.epam.tishkin.authorization;

import java.util.ArrayList;
import java.util.List;

public class Accounts {
   private List<Customer> accounts;

    public List<Customer> getAccounts() {
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        return this.accounts;
    }
}
