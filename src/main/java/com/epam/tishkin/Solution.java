package com.epam.tishkin;

import com.epam.tishkin.dao.UserDAO;
import com.epam.tishkin.dao.UserDatabaseDAO;
import com.epam.tishkin.exception.InvalidAutorizationException;
import com.epam.tishkin.library.LibraryAPI;
import com.epam.tishkin.models.User;
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

            UserDAO userDAO = new UserDatabaseDAO();
            User user = userDAO.userAuthorization(login, password);
            userDAO.setUser(user);
            LibraryAPI libraryAPI = new LibraryAPI(userDAO);
            libraryAPI.startLibraryUse();
        } catch (InvalidAutorizationException | IOException e) {
            logger.error(e.getMessage());
        }
    }
}


