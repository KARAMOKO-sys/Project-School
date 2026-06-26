package com.edueasy.course.service;

import com.edueasy.common.exception.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CurrentUserService {

    private static final Logger log = LoggerFactory.getLogger(CurrentUserService.class);

    /**
     * Récupère l'UUID de l'utilisateur actuel depuis le header de la requête
     * Pour le développement, on utilise un header X-User-Uuid
     * En production, on extrait l'UUID du token JWT
     */
    public String getCurrentUserUuid() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new UnauthorizedAccessException("No request context");
        }

        HttpServletRequest request = attributes.getRequest();

        // Option 1: Récupérer depuis le header X-User-Uuid (pour le développement)
        String userUuid = request.getHeader("X-User-Uuid");
        if (userUuid != null && !userUuid.isEmpty()) {
            return userUuid;
        }

        // Option 2: Récupérer depuis le token JWT
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Extraire l'UUID du token
            // return jwtUtil.extractUuid(token);
            // Pour le moment, on retourne un UUID par défaut pour le développement
            return "teacher-default-uuid";
        }

        throw new UnauthorizedAccessException("Missing or invalid Authorization header");
    }

    /**
     * Récupère le token JWT actuel
     */
    public String getCurrentToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new UnauthorizedAccessException("No request context");
        }

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new UnauthorizedAccessException("Missing or invalid Authorization header");
    }
}