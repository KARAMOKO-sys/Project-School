package com.edueasy.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret:}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long defaultExpiration;

    private SecretKey secretKey;
    private static final long INDEFINITE_EXPIRATION = 3153600000000L;
    public static final long EXPIRATION_1_HOUR = 3600000L;
    public static final long EXPIRATION_1_DAY = 86400000L;
    public static final long EXPIRATION_7_DAYS = 604800000L;
    public static final long EXPIRATION_30_DAYS = 2592000000L;

    public JwtUtil() {}

    @PostConstruct
    public void init() {
        log.info("Initializing JwtUtil...");
        String secretToUse = this.secret;
        if (secretToUse != null && !secretToUse.isEmpty() && !"your-secret-key".equals(secretToUse)) {
            this.initializeSecret(secretToUse);
            log.info("✅ JwtUtil successfully initialized with key size: {} bits",
                    this.secretKey.getEncoded().length * 8);
        } else {
            log.warn("⚠️ JWT_SECRET is not properly configured! Using a generated secure key.");
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            log.info("✅ Generated secure key with size: {} bits",
                    this.secretKey.getEncoded().length * 8);
        }
    }

    private void initializeSecret(String secretString) {
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(secretString);
            log.debug("Secret decoded from Base64, length: {} bytes", keyBytes.length);
        } catch (IllegalArgumentException e) {
            keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
            log.debug("Secret used as raw string, length: {} bytes", keyBytes.length);
        }

        if (keyBytes.length < 32) {
            log.error("❌ JWT secret is too weak! Got {} bits, need at least 256 bits.",
                    keyBytes.length * 8);
            log.error("Generating a secure key automatically...");
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            log.warn("⚠️ Using auto-generated key. For production, please configure a proper 256-bit key.");
        } else {
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        }
    }

    // ===== Validation =====

    public Boolean validateToken(String token, UserDetails userDetails) {
        if (token != null && !token.isEmpty() && userDetails != null) {
            try {
                String userUuid = this.extractUuid(token);
                return userUuid.equals(userDetails.getUsername()) && !this.isTokenExpired(token);
            } catch (Exception e) {
                log.warn("Token validation failed: {}", e.getMessage());
                return false;
            }
        }
        return false;
    }

    public Boolean validateToken(String token) {
        if (token != null && !token.isEmpty()) {
            this.ensureSecretKey();
            try {
                Jwts.parserBuilder()
                        .setSigningKey(this.secretKey)
                        .build()
                        .parseClaimsJws(token);
                return !this.isTokenExpired(token);
            } catch (ExpiredJwtException e) {
                log.warn("Token expired: {}", e.getMessage());
                return false;
            } catch (MalformedJwtException e) {
                log.warn("Malformed token: {}", e.getMessage());
                return false;
            } catch (SignatureException e) {
                log.warn("Invalid signature: {}", e.getMessage());
                return false;
            } catch (Exception e) {
                log.warn("Token validation failed: {}", e.getMessage());
                return false;
            }
        } else {
            log.warn("Token is null or empty");
            return false;
        }
    }

    public Boolean validateTokenWithUuid(String token, String uuid) {
        if (token != null && !token.isEmpty() && uuid != null && !uuid.isEmpty()) {
            try {
                String tokenUuid = this.extractUuid(token);
                return tokenUuid.equals(uuid) && !this.isTokenExpired(token);
            } catch (Exception e) {
                log.warn("Token validation failed: {}", e.getMessage());
                return false;
            }
        }
        return false;
    }

    // ===== Génération de tokens =====

    public String generateIndefiniteToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("type", "indefinite");
        return this.createToken(claims, email, INDEFINITE_EXPIRATION);
    }

    public String generateIndefiniteTokenUuid(String uuid, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("type", "indefinite");
        return this.createToken(claims, uuid, INDEFINITE_EXPIRATION);
    }

    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("type", "temporary");
        return this.createToken(claims, email, this.defaultExpiration);
    }

    public String generateToken(String email, String role, long expirationMillis) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("type", "temporary");
        return this.createToken(claims, email, expirationMillis);
    }

    public String generateServiceToken(String serviceName, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("type", "service");
        claims.put("service", serviceName);
        return this.createToken(claims, serviceName, INDEFINITE_EXPIRATION);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        this.ensureSecretKey();
        return Jwts.builder()
                .setClaims(claims)  // Utilisation de setClaims au lieu de claims()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ===== Extraction =====

    public String extractUuid(String token) {
        return this.extractClaim(token, Claims::getSubject);
    }

    public String extractEmail(String token) {
        return this.extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        try {
            return this.extractAllClaims(token).get("role", String.class);
        } catch (Exception e) {
            log.warn("Failed to extract role: {}", e.getMessage());
            return null;
        }
    }

    public String extractTokenType(String token) {
        try {
            return this.extractAllClaims(token).get("type", String.class);
        } catch (Exception e) {
            log.warn("Failed to extract token type: {}", e.getMessage());
            return null;
        }
    }

    public String extractService(String token) {
        try {
            return this.extractAllClaims(token).get("service", String.class);
        } catch (Exception e) {
            log.warn("Failed to extract service: {}", e.getMessage());
            return null;
        }
    }

    public Date extractExpiration(String token) {
        return this.extractClaim(token, Claims::getExpiration);
    }

    public Boolean isIndefiniteToken(String token) {
        String type = this.extractTokenType(token);
        return "indefinite".equals(type);
    }

    public Boolean isServiceToken(String token) {
        String type = this.extractTokenType(token);
        return "service".equals(type);
    }

    public Boolean isTemporaryToken(String token) {
        String type = this.extractTokenType(token);
        return "temporary".equals(type);
    }

    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = this.extractExpiration(token);
            return expiration != null && expiration.before(new Date());
        } catch (Exception e) {
            log.warn("Failed to check token expiration: {}", e.getMessage());
            return true;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        this.ensureSecretKey();
        return Jwts.parserBuilder()
                .setSigningKey(this.secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private void ensureSecretKey() {
        if (this.secretKey == null) {
            log.warn("SecretKey is null, reinitializing...");
            this.init();
        }
    }

    // ===== Méthodes supplémentaires =====

    public Long getExpirationInSeconds(String token) {
        try {
            Date expiration = this.extractExpiration(token);
            if (expiration == null) {
                return null;
            }
            long diff = expiration.getTime() - System.currentTimeMillis();
            return diff > 0 ? diff / 1000 : 0;
        } catch (Exception e) {
            log.warn("Failed to get expiration: {}", e.getMessage());
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        return validateToken(token);
    }

    public boolean isTokenExpired(String token, Date date) {
        try {
            Date expiration = this.extractExpiration(token);
            return expiration != null && expiration.before(date);
        } catch (Exception e) {
            log.warn("Failed to check token expiration: {}", e.getMessage());
            return true;
        }
    }

    public Map<String, Object> getAllClaims(String token) {
        try {
            Claims claims = this.extractAllClaims(token);
            return new HashMap<>(claims);
        } catch (Exception e) {
            log.warn("Failed to extract all claims: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}