package com.edueasy.user.service.impl;

import com.edueasy.common.dto.AdminResponseDTO;
import com.edueasy.common.enums.AdminPermission;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.model.Admin;
import com.edueasy.user.mapper.AdminMapper;
import com.edueasy.user.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
public class AdminInitializationService {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializationService.class);

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminMapper adminMapper;

    @Value("${admin.init.enabled:true}")
    private boolean adminInitEnabled;

    @Value("${admin.init.email:admin@edueasy.com}")
    private String adminEmail;

    @Value("${admin.init.password:Admin@123456}")
    private String adminPassword;

    @Value("${admin.init.first-name:Super}")
    private String adminFirstName;

    @Value("${admin.init.last-name:Admin}")
    private String adminLastName;

    public AdminInitializationService(AdminRepository adminRepository,
                                      PasswordEncoder passwordEncoder,
                                      AdminMapper adminMapper) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminMapper = adminMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeAdmin() {
        if (!adminInitEnabled) {
            log.info("Admin initialization is disabled. Set admin.init.enabled=true to enable.");
            return;
        }

        log.info("Checking if admin user exists...");

        if (adminRepository.count() == 0) {
            log.info("No admin found. Creating default administrator...");
            createDefaultAdmin();
        } else {
            log.info("Admin user(s) already exist. Skipping initialization.");
        }
    }

    private void createDefaultAdmin() {
        Admin admin = new Admin();
        admin.setUuid(UUID.randomUUID().toString());
        admin.setEmail(adminEmail);
        admin.setFirstName(adminFirstName);
        admin.setLastName(adminLastName);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setEmployeeNumber("ADM-" + System.currentTimeMillis());
        admin.setDepartment("Administration");
        admin.setPosition("Super Administrator");
        admin.setHireDate(LocalDateTime.now().toLocalDate());
        admin.setAccessLevel(5);
        admin.setPermissions(Arrays.asList(AdminPermission.values()));
        admin.setStatus(UserStatus.ACTIVE);
        admin.setLocale("fr");
        admin.setTimezone("Europe/Paris");
        admin.setManagedSchoolIds(new ArrayList<>());

        // NOTE: Les champs createdAt et updatedAt seront automatiquement gérés
        // par AuditTimestamps via @PrePersist

        admin = adminRepository.save(admin);

        log.info("========================================");
        log.info("Default administrator created successfully:");
        log.info("UUID: {}", admin.getUuid());
        log.info("Email: {}", adminEmail);
        log.info("Password: {}", adminPassword);
        log.info("Employee Number: {}", admin.getEmployeeNumber());
        log.info("Access Level: Super Admin (5)");
        log.info("========================================");
        log.info("IMPORTANT: Please change the password after first login!");
        log.info("========================================");
    }
}