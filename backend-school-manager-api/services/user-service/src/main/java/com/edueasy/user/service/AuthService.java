package com.edueasy.user.service;

import com.edueasy.common.dto.LoginRequestDTO;
import com.edueasy.common.dto.LoginResponseDTO;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.exception.UnauthorizedAccessException;
import com.edueasy.common.model.Admin;
import com.edueasy.common.model.SupportAgent;
import com.edueasy.common.model.TeacherSimple;
import com.edueasy.common.model.User;
import com.edueasy.common.security.JwtUtil;
import com.edueasy.user.repository.AdminRepository;
import com.edueasy.user.repository.SupportAgentRepository;
import com.edueasy.user.repository.TeacherSimpleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final AdminRepository adminRepository;
    private final TeacherSimpleRepository teacherRepository;
    private final SupportAgentRepository supportAgentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Authenticating user with email: {}", request.getEmail());
        User user = null;
        String role = null;
        Admin admin = (Admin)this.adminRepository.findByEmail(request.getEmail()).orElse((Admin) null);
        if (admin != null && this.passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            user = admin;
            role = "ADMINISTRATOR";
            log.info("Admin authenticated: {}", admin.getEmail());
        }

        if (user == null) {
            SupportAgent supportAgent = (SupportAgent)this.supportAgentRepository.findByEmail(request.getEmail()).orElse((SupportAgent) null);
            if (supportAgent != null && this.passwordEncoder.matches(request.getPassword(), supportAgent.getPasswordHash())) {
                user = supportAgent;
                role = "SUPPORT_AGENT";
                log.info("Support Agent authenticated: {}", supportAgent.getEmail());
            }
        }

        if (user == null) {
            TeacherSimple teacher = (TeacherSimple)this.teacherRepository.findByEmail(request.getEmail()).orElse((TeacherSimple) null);
            if (teacher != null && this.passwordEncoder.matches(request.getPassword(), teacher.getPasswordHash())) {
                user = teacher;
                role = "TEACHER_SIMPLE";
                log.info("Teacher authenticated: {}", teacher.getEmail());
            }
        }

        if (user == null) {
            log.warn("Login failed for email: {}", request.getEmail());
            throw new UnauthorizedAccessException("Invalid email or password");
        } else if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("Account is not active for email: {}", request.getEmail());
            throw new UnauthorizedAccessException("Account is not active. Please contact administrator.");
        } else {
            String token = this.jwtUtil.generateToken(user.getUuid(), role);
            return LoginResponseDTO.builder().token(token).tokenType("Bearer").expiresIn(86400000L).uuid(user.getUuid()).email(user.getEmail()).firstName(user.getFirstName()).lastName(user.getLastName()).role(role).build();
        }
    }

    public AuthService(final AdminRepository adminRepository, final TeacherSimpleRepository teacherRepository, final SupportAgentRepository supportAgentRepository, final PasswordEncoder passwordEncoder, final JwtUtil jwtUtil) {
        this.adminRepository = adminRepository;
        this.teacherRepository = teacherRepository;
        this.supportAgentRepository = supportAgentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
}
