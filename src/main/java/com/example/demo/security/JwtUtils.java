package com.example.demo.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    // Read secret key and expiration from application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    // Convert the plain secret string into a cryptographic Key object
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate a JWT token for the given username
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)                          // who this token belongs to
                .setIssuedAt(new Date())                       // when was this token created
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs)) // when it expires
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // sign with secret key
                .compact();                                    // build and return the token string
    }

    // Extract the username (subject) stored inside the token
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // Validate the token — checks signature and expiry
    public boolean validateToken(String token) {
        try {
            getClaims(token); // if this succeeds, token is valid
            return true;
        } catch (Exception e) {
            // Token is expired, tampered, or malformed
            return false;
        }
    }

    // Parse and return the claims (payload) from the token
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // use same key that was used to sign
                .build()
                .parseClaimsJws(token)          // parse and verify the token
                .getBody();                     // return the payload (claims)
    }
}
