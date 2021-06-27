package com.epam.tishkin.endpoint;

import com.epam.tishkin.ws.impl.LibraryImpl;
import com.epam.tishkin.ws.impl.VisitorImpl;
import jakarta.xml.ws.Endpoint;

public class LibraryPublisher {

    public static void main(String[] args) {
        Endpoint.publish("http://localhost:9999/ws/library", new LibraryImpl());
        Endpoint.publish("http://localhost:9999/ws/user", new VisitorImpl());
    }
}
