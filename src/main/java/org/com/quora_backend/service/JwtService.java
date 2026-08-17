package org.com.quora_backend.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.com.quora_backend.security.UserPrincipal;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // In production this comes from configuration (application.yml / env var),
    // never hardcoded — shown as a field here for clarity only.
    private final SecretKey signingKey = Keys.hmacShaKeyFor(
            "replace-with-a-real-256-bit-secret-from-config".getBytes());

    private static final long EXPIRATION_MS = 1000 * 60 * 60; // 1 hour

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String expectedUsername) {
        try {
            Claims claims = extractClaims(token);
            boolean usernameMatches = claims.getSubject().equals(expectedUsername);
            boolean notExpired = claims.getExpiration().after(new Date());
            return usernameMatches && notExpired;
        } catch (JwtException e) {
            return false; // malformed, tampered, or expired — treat as invalid, don't throw
        }
    }


    public String generateToken(UserPrincipal userPrincipal) {
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("role", userPrincipal.getUser().getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(signingKey)
                .compact();
    }
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
