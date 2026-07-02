/*
package com.edueasy.user.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
// import org.springframework.stereotype.Component;  // 🔥 Commenter cette ligne

import java.io.IOException;

// @Component  // 🔥 Désactiver ce filtre
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        // 🔥 CORRECTION : Vérifier que le header n'est pas déjà présent
        if (!response.containsHeader("Access-Control-Allow-Origin")) {
            response.setHeader("Access-Control-Allow-Origin",
                    request.getHeader("Origin") != null ? request.getHeader("Origin") : "*");
        }
        if (!response.containsHeader("Access-Control-Allow-Methods")) {
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");
        }
        if (!response.containsHeader("Access-Control-Max-Age")) {
            response.setHeader("Access-Control-Max-Age", "3600");
        }
        if (!response.containsHeader("Access-Control-Allow-Headers")) {
            response.setHeader("Access-Control-Allow-Headers",
                    "Authorization, Content-Type, Accept, Origin, X-User-Uuid, Access-Control-Request-Method, Access-Control-Request-Headers, X-Requested-With");
        }
        if (!response.containsHeader("Access-Control-Expose-Headers")) {
            response.setHeader("Access-Control-Expose-Headers", "Authorization, X-User-Uuid");
        }
        if (!response.containsHeader("Access-Control-Allow-Credentials")) {
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        // Répondre immédiatement aux requêtes OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation
    }

    @Override
    public void destroy() {
        // Nettoyage
    }
}

 */