package com.projexflow.apigateway.ProjexFlow_APIGateway.security;


import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    @Override
    public int getOrder() {
        return -1; // run early
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        // 1) Public endpoints (no token required)
        if (isPublic(path, method)) {
            return chain.filter(exchange);
        }

        // 2) Read token
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing Bearer token");
        }

        String token = authHeader.substring(7);

        // 3) Validate token + extract claims
        final String role;
        final String email;
        try {
            email = jwtService.email(token);
            role = jwtService.role(token);
        } catch (JwtException ex) {
            return unauthorized(exchange, "Invalid/expired token");
        }

        // 4) RBAC checks at gateway (adjust rules as you need)
        if (!isAllowed(path, method, role)) {
            return forbidden(exchange, "Forbidden for role: " + role);
        }

        // 5) Forward with identity headers (optional but useful)
        ServerWebExchange mutated = exchange.mutate()
                .request(r -> r.headers(h -> {
                    h.set("X-ROLE", role);
                    h.set("X-EMAIL", email);
                }))
                .build();

        return chain.filter(mutated);
    }

    private boolean isPublic(String path, HttpMethod method) {
        // login/register via UMS through gateway
        if (path.startsWith("/ums/auth/")) return true;

        // If you want public user creation
        if (method == HttpMethod.POST && path.startsWith("/ums/api/v1/")) return true;

        // allow actuator health checks if you expose them through gateway
        if (path.startsWith("/actuator/")) return true;

        return false;
    }

    private boolean isAllowed(String path, HttpMethod method, String role) {
        // Admin-only UMS management (non-POST)
        if (path.startsWith("/ums/api/v1/admins")) return "ADMIN".equals(role);

        // TMS rules
        if (path.startsWith("/tms/mentor")) return ("MENTOR".equals(role) || "ADMIN".equals(role));
        if (path.startsWith("/tms/student")) return ("STUDENT".equals(role) || "ADMIN".equals(role));

        // PMS/GMS/MAMS: require any authenticated role
        return ("ADMIN".equals(role) || "MENTOR".equals(role) || "STUDENT".equals(role));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String msg) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(("{\"message\":\"" + msg + "\"}").getBytes())));
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String msg) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(("{\"message\":\"" + msg + "\"}").getBytes())));
    }
}
