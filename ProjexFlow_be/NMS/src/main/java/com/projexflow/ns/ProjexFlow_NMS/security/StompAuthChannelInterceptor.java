package com.projexflow.ns.ProjexFlow_NMS.security;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

/**
 * Authenticates websocket clients at STOMP CONNECT time.
 *
 * Browser websocket handshakes can't reliably send Authorization headers.
 * So the React client should pass it as a STOMP connect header:
 *   connectHeaders: { Authorization: 'Bearer <jwt>' }
 */
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = firstNativeHeader(accessor, "Authorization");
            if (authHeader == null) {
                // Reject connection if no token is provided
                return null;
            }

            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            try {
                String role = jwtService.getRole(token);
                Long uid = jwtService.getUserId(token);

                // This name becomes the routing key for convertAndSendToUser(...)
                String userKey = role.toUpperCase() + ":" + uid;

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                Principal principal = new UsernamePasswordAuthenticationToken(userKey, null, authorities);
                accessor.setUser(principal);

            } catch (Exception e) {
                // invalid token => reject
                return null;
            }
        }

        return message;
    }

    private String firstNativeHeader(StompHeaderAccessor accessor, String name) {
        List<String> values = accessor.getNativeHeader(name);
        if (values == null || values.isEmpty()) return null;
        return values.get(0);
    }
}
