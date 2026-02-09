package com.projexflow.ns.ProjexFlow_NMS.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Service
public class JwtService {
    private final Key key;

    public JwtService(@Value("${security.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public Long getUserId(String token) {
        Object v = parse(token).getBody().get("uid");
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof Long l) return l;
        return Long.valueOf(String.valueOf(v));
    }

    public String getRole(String token) {
        return parse(token).getBody().get("role", String.class);
    }

    public String getEmail(String token) {
        return parse(token).getBody().getSubject();
    }
}
