package com.epam.tishkin.server;

import com.epam.tishkin.server.rs.filter.UserAuthFilter;
import com.epam.tishkin.server.rs.filter.UserRoleFilter;
import com.epam.tishkin.server.rs.resource.AuthorREST;
import com.epam.tishkin.server.rs.resource.BookREST;
import com.epam.tishkin.server.rs.config.AutoScanFeature;
import com.epam.tishkin.server.rs.resource.BookmarkREST;
import com.epam.tishkin.server.rs.resource.UserREST;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import java.net.URI;

public class MainApp {
    public static final String BASE_URI = "http://localhost:8083/homeLibrary/";

    public static void main(String[] args) {
        final ResourceConfig config = new ResourceConfig();
        config.register(AutoScanFeature.class);
        config.register(BookREST.class);
        config.register(UserREST.class);
        config.register(AuthorREST.class);
        config.register(BookmarkREST.class);
        config.register(UserRoleFilter.class);
        config.register(UserAuthFilter.class);
        GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }
}
