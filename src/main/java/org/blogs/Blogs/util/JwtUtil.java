package org.blogs.Blogs.util;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secretKey;
    @Value("${app.jwt.expiration}")
    private long expiredTime;

    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validate(String token, UserDetails details){
        try{
        String username = extractUsername(token);
        return username.equals(details.getUsername());}
        catch (Exception e){
            return false;
        }
    }
}
