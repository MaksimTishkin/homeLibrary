package com.epam.tishkin;

import com.epam.tishkin.authorization.AccountsList;
import com.epam.tishkin.authorization.handler.Handler;
import com.epam.tishkin.authorization.handler.LoginHandler;
import com.epam.tishkin.authorization.exception.InvalidAutorizationException;
import com.epam.tishkin.client.Visitor;
import com.epam.tishkin.library.Author;
import com.epam.tishkin.library.Book;
import com.epam.tishkin.library.Library;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Solution {
    static Visitor visitor;

    public static void main(String[] args) {
        String login;
        String password;

        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter your login");
            login = consoleReader.readLine();
            System.out.println("Enter your password");
            password = consoleReader.readLine();
            authorization(login, password);
            visitor.startLibraryUse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void authorization(String login, String password) {
        try {
            Handler authorization = new LoginHandler(login, password);
            visitor = authorization.check();
            System.out.println("Hi");
        } catch (InvalidAutorizationException e) {
            System.out.println("Invalid login/password");
        }
    }
}
