package com.michael.blog.security;

import com.michael.blog.constants.SecurityConstant;
import com.michael.blog.exception.payload.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;



    //generate token

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        String token = Jwts.builder()
                .setIssuer(SecurityConstant.MICHAEL_ROYF_LLC)
                .setAudience(SecurityConstant.MICHAEL_ROYF_ADMINISTRATION)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME_FOR_ACCESS_TOKEN))
                .signWith(key())
                .compact();
        return token;
    }

    //get username from jwt token
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }


    //validate Jet Token
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

    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }


}
