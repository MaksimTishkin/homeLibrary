package com.epam.tishkin;

import com.epam.tishkin.dao.UserDAO;
import com.epam.tishkin.dao.impl.UserDatabaseDAO;
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

            UserDAO userDAO = new UserDatabaseDAO();
            if (userDAO.userAuthorization(login, password)) {
                LibraryAPI libraryAPI = new LibraryAPI(userDAO);
                libraryAPI.startLibraryUse();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}


