package com.example.springbootlearning.security;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

import javax.crypto.SecretKey;


@Service
public class JwtService { //负责JWT 的生成和解析。
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;


    public String generateToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }


    private SecretKey getSigningKey() {   //private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(
                        StandardCharsets.UTF_8
                )
        );
    }



    // 提取token中的名字.
    private Claims extractAllClaims( String token ) {
        return Jwts.parser()
                .verifyWith( (SecretKey) getSigningKey() )
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername( String token) {
        return extractAllClaims(token)
                .getSubject();
    }
    // 检查过期时间
    private Date extractExpiration( String token) {
        return extractAllClaims(token).getExpiration();
    }
    private boolean isTokenExpired( String token) {
        return extractExpiration(token).before(new Date());
    }
    
    //验证 Token
    public boolean isTokenValid( String token, UserDetails userDetails ){
                String username = extractUsername(token);
                return username.equals( userDetails.getUsername())
                        && !isTokenExpired(token);
    }



}
