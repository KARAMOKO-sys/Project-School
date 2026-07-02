package com.edueasy.gateway.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class CorsPreflightWebFilter {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebFilter corsPreflightFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            // 🔥 Intercepter les requêtes OPTIONS avant tout traitement
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                ServerHttpResponse response = exchange.getResponse();
                HttpHeaders headers = response.getHeaders();

                // Ajouter TOUS les headers CORS
                headers.set("Access-Control-Allow-Origin", "http://localhost:4200");
                headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");
                headers.set("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, Origin, X-User-Uuid, Access-Control-Request-Method, Access-Control-Request-Headers, X-Requested-With");
                headers.set("Access-Control-Allow-Credentials", "true");
                headers.set("Access-Control-Max-Age", "3600");

                // Répondre immédiatement avec 200 OK
                response.setStatusCode(HttpStatus.OK);
                return response.setComplete();
            }

            // Pour les autres requêtes, continuer la chaîne
            return chain.filter(exchange);
        };
    }
}