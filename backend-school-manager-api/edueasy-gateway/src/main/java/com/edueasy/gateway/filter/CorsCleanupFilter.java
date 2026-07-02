package com.edueasy.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class CorsCleanupFilter {

    // Garder le filtre pour nettoyer les headers CORS des réponses POST/GET
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public GlobalFilter corsCleanupGlobalFilter() {
        return (exchange, chain) -> {
            // Ne pas traiter les OPTIONS ici (déjà géré par le WebFilter)
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                return chain.filter(exchange);
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                ServerHttpResponse response = exchange.getResponse();
                HttpHeaders headers = response.getHeaders();

                // Nettoyer et forcer Access-Control-Allow-Origin
                if (headers.containsKey("Access-Control-Allow-Origin")) {
                    List<String> origins = headers.get("Access-Control-Allow-Origin");
                    if (origins != null && !origins.isEmpty()) {
                        String firstOrigin = origins.get(0);
                        if (firstOrigin.contains(",")) {
                            firstOrigin = firstOrigin.split(",")[0].trim();
                        }
                        headers.remove("Access-Control-Allow-Origin");
                        headers.set("Access-Control-Allow-Origin", firstOrigin);
                    }
                } else {
                    headers.set("Access-Control-Allow-Origin", "http://localhost:4200");
                }

                // Nettoyer les autres headers
                if (headers.containsKey("Access-Control-Allow-Methods")) {
                    List<String> methods = headers.get("Access-Control-Allow-Methods");
                    if (methods != null && !methods.isEmpty()) {
                        String firstMethod = methods.get(0);
                        if (firstMethod.contains(",")) {
                            firstMethod = firstMethod.split(",")[0].trim();
                        }
                        headers.remove("Access-Control-Allow-Methods");
                        headers.set("Access-Control-Allow-Methods", firstMethod);
                    }
                }

                if (headers.containsKey("Access-Control-Allow-Headers")) {
                    List<String> headerValues = headers.get("Access-Control-Allow-Headers");
                    if (headerValues != null && !headerValues.isEmpty()) {
                        String firstHeader = headerValues.get(0);
                        if (firstHeader.contains(",")) {
                            firstHeader = firstHeader.split(",")[0].trim();
                        }
                        headers.remove("Access-Control-Allow-Headers");
                        headers.set("Access-Control-Allow-Headers", firstHeader);
                    }
                }

                // Forcer le header Credentials
                headers.set("Access-Control-Allow-Credentials", "true");
            }));
        };
    }
}