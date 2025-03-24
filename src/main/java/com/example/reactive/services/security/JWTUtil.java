package com.example.reactive.services.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JWTUtil {

    private static final Duration EXPIRATION_TIME = Duration.ofMinutes(10);

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String generateToken(String username) {
        return Jwts.builder()
                .claims(new HashMap<>())
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME.toMillis()))
                .signWith(key).compact();
    }

    public static Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
