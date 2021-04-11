package com.epam.tishkin.client;

import com.epam.tishkin.authorization.Account;
import com.epam.tishkin.authorization.AccountsList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class Administrator extends Visitor {
    private AccountsList accountsList;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Administrator(String name) {
        super(name);
    }

    public void addNewUser(String login, String password) {
        try (FileReader reader = new FileReader("src/main/resources/accounts.json")) {
            accountsList = gson.fromJson(reader, AccountsList.class);
            accountsList.getAccounts().add(new Account(login, password));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter("src/main/resources/accounts.json")) {
            writer.write(gson.toJson(accountsList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void blockUser(String login) {
        try (FileReader reader = new FileReader("src/main/resources/accounts.json")) {
            accountsList = gson.fromJson(reader, AccountsList.class);
            Optional<Account> account = accountsList.getAccounts()
                    .stream()
                    .filter(a -> a.getLogin().equals(login))
                    .findFirst();
            account.ifPresent(System.out::println);
            account.ifPresent(value -> accountsList.getAccounts().remove(value));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter("src/main/resources/accounts.json")) {
            writer.write(gson.toJson(accountsList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showHistory() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/history.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}