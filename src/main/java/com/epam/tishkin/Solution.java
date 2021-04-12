package com.epam.tishkin;

import com.epam.tishkin.authorization.handler.Handler;
import com.epam.tishkin.authorization.handler.LoginHandler;
import com.epam.tishkin.authorization.exception.InvalidAutorizationException;
import com.epam.tishkin.client.Visitor;
import com.epam.tishkin.library.Library;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Solution {
    final static Logger logger = LogManager.getLogger(Solution.class);
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
        } catch (InvalidAutorizationException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static void authorization(String login, String password) throws InvalidAutorizationException {
            Handler authorization = new LoginHandler(login, password);
            visitor = authorization.check();
            System.out.println("Hi");

    }
}
