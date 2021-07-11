package com.epam.tishkin.server.rs.resource;

import com.epam.tishkin.models.User;
import com.epam.tishkin.server.rs.config.TokenManager;
import com.epam.tishkin.server.rs.service.LibraryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserREST {
    TokenManager tokenManager = new TokenManager();

    @Inject
    private LibraryService libraryService;

    @GET
    @Path("/authorization")
    public Response authorization(
            @HeaderParam("login") String login,
            @HeaderParam("password") String password) {
       User user = libraryService.authorization(login, password);
       if (user == null) {
           return Response.status(401).build();
       }
       String jwt = tokenManager.createToken(user);
       return Response.status(200).entity(jwt).build();
    }

    @GET
    @Path("/role")
    public Response getRole(@HeaderParam("token") String jwt) {
        String role = tokenManager.getRoleFromJWT(jwt);
        return Response.status(200).entity(role).build();
    }
}
