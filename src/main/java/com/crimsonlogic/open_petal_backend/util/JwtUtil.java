package com.crimsonlogic.open_petal_backend.util;

import com.crimsonlogic.open_petal_backend.enumerator.RoleType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, RoleType role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())
                .claim("loginTime", now.getTime())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateVerificationToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (24 * 60 * 60 * 1000)); // 24 hours

        return Jwts.builder()
                .setSubject(email)
                .claim("type", "EMAIL_VERIFICATION")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generatePasswordResetToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (15 * 60 * 1000)); // 15 minutes

        return Jwts.builder()
                .setSubject(email)
                .claim("type", "PASSWORD_RESET")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractIssuedAt(String token) {
        return extractAllClaims(token).getIssuedAt();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            return !extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
