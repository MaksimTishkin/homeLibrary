package com.epam.tishkin.ws.endpoint;

import com.epam.tishkin.ws.impl.LibraryVisitorImpl;
import jakarta.xml.ws.Endpoint;

public class LibraryPublisher {

    public static void main(String[] args) {
        Endpoint.publish("http://localhost:9999/ws/library", new LibraryVisitorImpl());
    }
}
