package com.epam.tishkin.client;

import com.epam.tishkin.authorization.Account;
import com.epam.tishkin.authorization.AccountsList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class Administrator extends Visitor {
    private static final Properties properties = new Properties();
    final static Logger logger = LogManager.getLogger(Administrator.class);
    private AccountsList accountsList;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Administrator(String name) {
        super(name);
    }

    public void addNewUser(String login, String password) {
        try (FileReader readerForProperties = new FileReader("src/main/resources/config.properties")) {
            properties.load(readerForProperties);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        try (FileReader reader = new FileReader(properties.getProperty("pathFromAccounts"))) {
            accountsList = gson.fromJson(reader, AccountsList.class);
            accountsList.getAccounts().add(new Account(login, password));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        try (FileWriter writer = new FileWriter(properties.getProperty("pathFromAccounts"))) {
            writer.write(gson.toJson(accountsList));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void blockUser(String login) {
        try (FileReader readerForProperties = new FileReader("src/main/resources/config.properties")) {
            properties.load(readerForProperties);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        try (FileReader reader = new FileReader(properties.getProperty("pathFromAccounts"))) {
            accountsList = gson.fromJson(reader, AccountsList.class);
            Optional<Account> account = accountsList.getAccounts()
                    .stream()
                    .filter(a -> a.getLogin().equals(login))
                    .findFirst();
            account.ifPresent(System.out::println);
            account.ifPresent(value -> accountsList.getAccounts().remove(value));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        try (FileWriter writer = new FileWriter(properties.getProperty("pathFromAccounts"))) {
            writer.write(gson.toJson(accountsList));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void showHistory() {
        try (FileReader readerForProperties = new FileReader("src/main/resources/config.properties")) {
            properties.load(readerForProperties);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(properties.getProperty("pathFromHistory")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println(" ");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}