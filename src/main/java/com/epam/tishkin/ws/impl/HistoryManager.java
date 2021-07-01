package com.epam.tishkin.ws.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HistoryManager {
    private static final Properties properties = new Properties();
    final static Logger logger = LogManager.getLogger(HistoryManager.class);

    static {
        try (FileReader readerForProperties = new FileReader("src/main/resources/config.properties")) {
            properties.load(readerForProperties);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static synchronized void write(String login, String message) {
        try (FileWriter fileWriter = new FileWriter(properties.getProperty("pathFromHistory"), true)) {
            fileWriter.write(login + "- " + message + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> read() {
        List<String> fullHistory = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(properties.getProperty("pathFromHistory")))) {
                String line;
                while((line = reader.readLine()) != null) {
                    fullHistory.add(line);
                }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return fullHistory;
    }
}

