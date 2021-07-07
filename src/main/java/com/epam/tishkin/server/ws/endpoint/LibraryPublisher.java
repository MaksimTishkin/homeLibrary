package com.epam.tishkin.server.ws.endpoint;

import com.epam.tishkin.server.ws.impl.LibraryVisitorImpl;
import jakarta.xml.ws.Endpoint;

public class LibraryPublisher {

    public static void main(String[] args) {
        Endpoint.publish("http://localhost:9999/ws/library", new LibraryVisitorImpl());
    }
}
