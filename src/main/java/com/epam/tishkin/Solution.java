package com.epam.tishkin;

import com.epam.tishkin.authorization.LoginHandler;
import com.epam.tishkin.authorization.exceptions.InvalidAutorizationException;
import com.epam.tishkin.client.Person;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Solution {

    public static void main(String[] args) throws IOException {
        String login;
        String password;

        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter your login");
            login = consoleReader.readLine();
            System.out.println("Enter your password");
            password = consoleReader.readLine();

        }
        try {
            LoginHandler authorization = new LoginHandler();
            Person person = authorization.check(login, password);
            System.out.println("Hi");
        } catch (InvalidAutorizationException e) {
            System.out.println("Invalid login/password");
        }
    }
}
