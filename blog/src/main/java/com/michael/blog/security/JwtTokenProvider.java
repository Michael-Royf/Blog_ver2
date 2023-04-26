package com.michael.blog.security;

import com.michael.blog.exception.payload.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    public static final long EXPIRATION_TIME_FOR_ACCESS_TOKEN = 720_000;
    public static final long EXPIRATION_TIME_FOR_REFRESH_TOKEN = 3_600_000; // 60 min
    //604_800_000 7 days

    // generate access JWT token
    public String generateAccessToken(String username) {
        return generateToken(username, EXPIRATION_TIME_FOR_ACCESS_TOKEN);
    }

    // generate refresh JWT token
    public String generateToken(String username) {
        return generateToken(username, EXPIRATION_TIME_FOR_REFRESH_TOKEN);
    }

    // generate token
    private String generateToken(String username, long expirationTime) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key())
                .compact();
    }


    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    // get username from Jwt token
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        String username = claims.getSubject();
        return username;
    }

    // validate Jwt token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new TokenException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new TokenException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new TokenException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new TokenException("JWT claims string is empty.");
        }
    }
}


