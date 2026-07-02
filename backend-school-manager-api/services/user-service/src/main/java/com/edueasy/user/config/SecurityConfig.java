package com.edueasy.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    private static final String[] PUBLIC_URLS = {
            "/auth/**",
            "/teachers-simple/**",
            "/api/teachers-simple/**",
            "/api/students/register",
            "/api/students/register-simple",
            "/api/students-simple/register-simple",
            "/api/teachers-simple/register",
            "/api/teachers-simple/register-simple",
            "/api/support-agents/staff/register",

            // 🔥 AJOUTER CES LIGNES
            "/students-simple/**",
            "/api/students-simple/**",
            "/api/students-simple/register",
            "/api/students-simple/register-simple",
            "/api/students/register",
            "/api/students/register-simple",

            // Swagger
            "/api/swagger-ui/**",
            "/api/swagger-ui/*",
            "/api/swagger-ui/index.html",
            "/api/swagger-ui/swagger-initializer.js",
            "/api/swagger-ui/swagger-ui.css",
            "/api/swagger-ui/swagger-ui-bundle.js",
            "/api/swagger-ui/swagger-ui-standalone-preset.js",
            "/api/swagger-ui/favicon-32x32.png",
            "/api/swagger-ui/favicon-16x16.png",
            "/api/v3/api-docs/**",
            "/api/v3/api-docs/*",
            "/api/v3/api-docs/swagger-config",
            "/swagger-ui/**",
            "/swagger-ui/*",
            "/swagger-ui/index.html",
            "/v3/api-docs/**",
            "/v3/api-docs/*",
            "/api/actuator/health",
            "/actuator/health",
            "/api/error",
            "/error"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 🔥 Utiliser setAllowedOrigins au lieu de setAllowedOriginPatterns
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "http://localhost:4201",
                "http://127.0.0.1:4200"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-User-Uuid",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-Requested-With"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-User-Uuid"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}