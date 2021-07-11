package com.epam.tishkin.server.rs.filter;

import com.epam.tishkin.server.rs.config.TokenManager;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Priority(Priorities.AUTHENTICATION)
@Provider
@UserAuth
public class UserAuthFilter implements ContainerRequestFilter {
    TokenManager tokenManager = new TokenManager();

    public void filter(ContainerRequestContext request) {
        String jwt = request.getHeaderString("token");
        if (jwt.isEmpty() || !tokenManager.verifyToken(jwt)) {
            Response response = Response
                    .status(Response.Status.FORBIDDEN)
                    .build();
            request.abortWith(response);
        }
    }
}
