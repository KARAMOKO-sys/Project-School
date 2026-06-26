package com.edueasy.user.controller;

import com.edueasy.common.dto.ApiResponse;
import com.edueasy.common.dto.LoginRequestDTO;
import com.edueasy.common.dto.LoginResponseDTO;
import com.edueasy.user.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/auth"})
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @PostMapping({"/login"})
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO request) {
        log.info("POST /api/auth/login - Login attempt for email: {}", request.getEmail());
        LoginResponseDTO response = this.authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }
}