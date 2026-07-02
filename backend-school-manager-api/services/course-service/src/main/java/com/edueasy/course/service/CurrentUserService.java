package com.edueasy.course.service;

import com.edueasy.common.security.JwtUtil;
import com.edueasy.common.exception.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CurrentUserService {

    private static final Logger log = LoggerFactory.getLogger(CurrentUserService.class);

    private final JwtUtil jwtUtil;

    public CurrentUserService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Récupère l'UUID de l'utilisateur actuel depuis le contexte Spring Security
     */
    public String getCurrentUserUuid() {
        try {
            // Récupérer depuis le contexte Spring Security
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getPrincipal())) {
                String uuid = authentication.getName();
                log.debug("✅ UUID extrait du contexte Security: {}", uuid);
                return uuid;
            }
        } catch (Exception e) {
            log.warn("⚠️ Impossible d'extraire l'UUID du contexte: {}", e.getMessage());
        }

        // Fallback: récupérer depuis le token JWT
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    String uuid = jwtUtil.extractUuid(token);
                    if (uuid != null) {
                        log.debug("✅ UUID extrait du token: {}", uuid);
                        return uuid;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("⚠️ Impossible d'extraire l'UUID du token: {}", e.getMessage());
        }

        throw new UnauthorizedAccessException("User not authenticated");
    }

    /**
     * Récupère le token JWT actuel
     */
    public String getCurrentToken() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                throw new UnauthorizedAccessException("No request context");
            }

            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la récupération du token: {}", e.getMessage());
        }
        throw new UnauthorizedAccessException("Missing or invalid Authorization header");
    }
}