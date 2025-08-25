package com.himnario_service.himnario_service.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@AllArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {
    private final JwtService jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            // Solo para CONNECT: extraer y validar token
            String token = extractToken(accessor);

            if (token == null || token.isBlank()) {
                throw new MessagingException("Token no proporcionado");
            }

            if (!jwtTokenProvider.validateToken(token)) {
                throw new MessagingException("Token inválido");
            }

            Authentication auth = jwtTokenProvider.getAuthentication(token);
            if (auth == null) {
                throw new MessagingException("No se pudo obtener autenticación del token");
            }

            SecurityContextHolder.getContext().setAuthentication(auth);

            // Guardar la autenticación en la sesión para comandos posteriores
            accessor.getSessionAttributes().put("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        } else if (accessor.getCommand() == StompCommand.SUBSCRIBE || accessor.getCommand() == StompCommand.SEND) {
            // Para SUBSCRIBE y SEND: usar autenticación de la sesión
            Object securityContext = accessor.getSessionAttributes().get("SPRING_SECURITY_CONTEXT");

            if (securityContext instanceof SecurityContext) {
                SecurityContextHolder.setContext((SecurityContext) securityContext);
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                if (auth != null && auth.isAuthenticated()) {
                    // Validar acceso específico para suscripciones
                    if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
                        validateSubscriptionAccess(accessor, auth);
                    }
                } else {
                    throw new MessagingException("Sesión no autenticada");
                }
            } else {
                throw new MessagingException("Sesión no autenticada");
            }
        }

        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        List<String> authHeaders = accessor.getNativeHeader("Authorization");

        if (authHeaders == null || authHeaders.isEmpty()) {
            authHeaders = accessor.getNativeHeader("authorization");
        }

        if (authHeaders == null || authHeaders.isEmpty()) {
            return null;
        }

        String bearerToken = authHeaders.get(0);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private void validateSubscriptionAccess(StompHeaderAccessor accessor, Authentication auth) {
        String destination = accessor.getDestination();

        // Validar acceso a /topic/asesorias/{asesoriaId}
        if (destination != null && destination.startsWith("/topic/asesorias/")) {
            Long asesoriaId = extractAsesoriaId(destination);
            if (!tieneAccesoALaAsesoria(auth, asesoriaId)) {
                throw new MessagingException("No tienes permiso para suscribirte a este tema");
            }
        }

        // Validar acceso a rutas de usuario específico
        if (destination != null && destination.startsWith("/user/")) {
            String[] parts = destination.split("/");
            if (parts.length >= 3) {
                String userIdFromDestination = parts[2];
                String authenticatedUsername = auth.getName();

                // Obtener el ID del usuario autenticado desde la base de datos
                Long authenticatedUserId = getUserIdFromUsername(authenticatedUsername);

                // Si podemos obtener el ID, validar que coincida
                if (authenticatedUserId != null) {
                    if (!userIdFromDestination.equals(authenticatedUserId.toString())) {
                        throw new MessagingException("No tienes permiso para suscribirte a las notificaciones de otro usuario");
                    }
                }
                // Si no podemos obtener el ID, permitir acceso temporalmente
            }
        }
    }

    private Long extractAsesoriaId(String destination) {
        try {
            String[] parts = destination.split("/");
            return Long.parseLong(parts[3]); // Extrae el ID de "/topic/asesorias/123"
        } catch (Exception e) {
            throw new MessagingException("Ruta de suscripción inválida");
        }
    }

    private boolean tieneAccesoALaAsesoria(Authentication auth, Long asesoriaId) {
        // Implementa tu lógica de acceso (ej: verificar si el usuario es estudiante/asesor)
        String username = auth.getName();
        //return asesoriaService.verificarAcceso(username, asesoriaId);
        return true;
    }

    private Long getUserIdFromUsername(String username) {
        // TODO: Implementar la lógica para obtener el ID del usuario desde el username
        // En un sistema real, harías una consulta a la base de datos
        // Ejemplo: return userRepository.findByUsername(username).getId();

        // Por ahora, retornar null permite el acceso temporalmente
        return null;
    }
}
