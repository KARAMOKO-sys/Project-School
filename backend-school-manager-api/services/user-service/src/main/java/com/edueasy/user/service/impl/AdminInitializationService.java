package com.edueasy.user.service.impl;

import com.edueasy.common.model.Admin;
import com.edueasy.common.model.User;
import com.edueasy.common.enums.UserRole;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.user.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminInitializationService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@edueasy.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123456}")
    private String adminPassword;

    @Value("${app.admin.first-name:Super}")
    private String adminFirstName;

    @Value("${app.admin.last-name:Admin}")
    private String adminLastName;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeAdmin() {
        log.info("Checking if admin user exists...");

        // Vérifier si un admin existe déjà
        if (adminRepository.count() > 0) {
            log.info("Admin user(s) already exist. Skipping initialization.");
            return;
        }

        log.info("No admin found. Creating default administrator...");

        try {
            // Créer l'utilisateur Admin
            Admin admin = new Admin();

            // 🔥 CHAMPS OBLIGATOIRES - AJOUTER username
            admin.setUsername("admin");  // ← AJOUTER CETTE LIGNE
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setFirstName(adminFirstName);
            admin.setLastName(adminLastName);
            admin.setRole(UserRole.ADMINISTRATOR);
            admin.setStatus(UserStatus.ACTIVE);

            // Initialiser les champs d'audit
            admin.initializeAuditFields();

            // Générer un UUID si nécessaire
            if (admin.getUuid() == null) {
                admin.setUuid(UUID.randomUUID().toString());
            }

            // Champs spécifiques à Admin
            admin.generateEmployeeNumber();
            admin.setAccessLevel(5); // Super Admin
            admin.setDepartment("Administration");
            admin.setPosition("Super Administrator");

            // Sauvegarder
            adminRepository.save(admin);

            log.info("========================================");
            log.info("Default administrator created successfully:");
            log.info("UUID: {}", admin.getUuid());
            log.info("Email: {}", admin.getEmail());
            log.info("Username: {}", admin.getUsername());
            log.info("Password: {}", adminPassword);
            log.info("Employee Number: {}", admin.getEmployeeNumber());
            log.info("Access Level: Super Admin (5)");
            log.info("========================================");
            log.info("IMPORTANT: Please change the password after first login!");
            log.info("========================================");

        } catch (Exception e) {
            log.error("Failed to create default administrator", e);
            throw new RuntimeException("Failed to initialize admin user", e);
        }
    }
}