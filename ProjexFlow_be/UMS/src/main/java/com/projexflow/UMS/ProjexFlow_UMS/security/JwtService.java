package com.projexflow.UMS.ProjexFlow_UMS.security;

import org.springframework.stereotype.Service;
import com.projexflow.UMS.ProjexFlow_UMS.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final Key key;
    private final long expirationMs;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms}") long expirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String email, Long domainUserId, Role role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(exp)
                .addClaims(Map.of(
                        "uid", domainUserId,
                        "role", role.name()
                ))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public String getEmail(String token) {
        return parse(token).getBody().getSubject();
    }

    public Long getUserId(String token) {
        Object v = parse(token).getBody().get("uid");
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof Long l) return l;
        return Long.valueOf(String.valueOf(v));
    }

    public Role getRole(String token) {
        return Role.valueOf(parse(token).getBody().get("role", String.class));
    }
}
