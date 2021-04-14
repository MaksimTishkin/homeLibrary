package com.epam.tishkin;

import com.epam.tishkin.authorization.handler.Handler;
import com.epam.tishkin.authorization.handler.LoginHandler;
import com.epam.tishkin.authorization.exception.InvalidAutorizationException;
import com.epam.tishkin.client.Visitor;
import com.epam.tishkin.library.LibraryAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Solution {
    final static Logger logger = LogManager.getLogger(Solution.class);

    public static void main(String[] args) {
        String login;
        String password;
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter your login");
            login = consoleReader.readLine();
            System.out.println("Enter your password");
            password = consoleReader.readLine();
            Visitor visitor = authorization(login, password);
            LibraryAPI libraryAPI = new LibraryAPI(visitor);
            libraryAPI.startLibraryUse();
        } catch (InvalidAutorizationException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static Visitor authorization(String login, String password) throws InvalidAutorizationException {
            Handler handler = new LoginHandler(login, password);
            return handler.check();
    }
}
