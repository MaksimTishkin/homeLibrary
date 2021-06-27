package com.epam.tishkin.ws.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class HistoryWriter {
    private static final Properties properties = new Properties();
    final static Logger logger = LogManager.getLogger(HistoryWriter.class);

    public static void write(String login, String message) {
        try (FileReader readerForProperties = new FileReader("src/main/resources/config.properties")) {
            properties.load(readerForProperties);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        try (FileWriter fileWriter = new FileWriter(properties.getProperty("pathFromHistory"), true)) {
            fileWriter.write(login + "- " + message + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
