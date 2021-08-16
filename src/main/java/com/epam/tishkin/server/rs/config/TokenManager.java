package com.epam.tishkin.server.rs.config;

import com.epam.tishkin.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class TokenManager {
    private final static Key KEY;
    public static final String AUTHORIZATION_PROPERTY = "token";
    private final static String LOGIN_PARAM = "login";
    private final static String ROLE_PARAM = "role";

    static {
        KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String createToken(User user) {
        Claims claims = Jwts.claims();
        claims.put(LOGIN_PARAM, user.getLogin());
        claims.put(ROLE_PARAM, user.getRole());
        return Jwts.builder()
                .setClaims(claims)
                .signWith(KEY)
                .compact();
    }

    public boolean verifyToken(String jws) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(jws);
        } catch (JwtException e) {
            return false;
        }
        return true;
    }

    public String getRoleFromJWT(String jwt) {
       Claims claims = Jwts.parserBuilder()
               .setSigningKey(KEY)
               .build()
               .parseClaimsJws(jwt)
               .getBody();
       return claims.get(ROLE_PARAM).toString();
    }

    public String getLoginFromJWT(String jwt) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        return claims.get(LOGIN_PARAM).toString();
    }
}
