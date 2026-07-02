package com.edueasy.user.service;

import com.edueasy.common.exception.UnauthorizedAccessException;
import com.edueasy.common.model.Admin;
import com.edueasy.common.model.StudentSimple;
import com.edueasy.common.model.SupportAgent;
import com.edueasy.common.model.SupportStaff;
import com.edueasy.common.model.TeacherSimple;
import com.edueasy.common.model.User;
import com.edueasy.user.repository.AdminRepository;
import com.edueasy.user.repository.StudentSimpleRepository;
import com.edueasy.user.repository.SupportAgentRepository;
import com.edueasy.user.repository.SupportStaffRepository;
import com.edueasy.user.repository.TeacherSimpleRepository;
//import com.edueasy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final AdminRepository adminRepository;
    private final SupportAgentRepository supportAgentRepository;
    private final SupportStaffRepository supportStaffRepository;
    private final TeacherSimpleRepository teacherSimpleRepository;
    private final StudentSimpleRepository studentSimpleRepository;
   // private final UserRepository userRepository;

    /**
     * Récupère l'UUID de l'utilisateur courant
     */
    public String getCurrentUserUuid() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof UserDetails) {
                    String username = ((UserDetails) principal).getUsername();
                    log.debug("✅ UUID extrait du token: {}", username);
                    return username;
                } else if (principal instanceof String) {
                    String uuid = (String) principal;
                    log.debug("✅ UUID extrait du token: {}", uuid);
                    return uuid;
                }
            }
        } catch (Exception e) {
            log.warn("⚠️ Impossible d'extraire l'UUID du token: {}", e.getMessage());
        }

        log.info("🔍 Mode développement: recherche d'un admin en base");

        try {
            Optional<Admin> adminOpt = adminRepository.findByEmail("admin@edueasy.com");
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                log.info("✅ Admin trouvé par email: {}", admin.getUuid());
                return admin.getUuid();
            }

            List<Admin> admins = adminRepository.findAll();
            if (!admins.isEmpty()) {
                Admin admin = admins.get(0);
                log.info("✅ Premier admin trouvé: {}", admin.getUuid());
                return admin.getUuid();
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération de l'admin: {}", e.getMessage());
        }

        log.warn("⚠️ Aucun admin trouvé, utilisation d'un UUID par défaut");
        return "1c387650-1d84-4b5a-9986-764944c0cead";
    }

    /**
     * Récupère l'utilisateur courant
     */
    /*
    public Optional<User> getCurrentUser() {
        String userUuid = getCurrentUserUuid();
        return userRepository.findByUuid(userUuid);
    }

     */

    /**
     * Récupère l'Admin courant
     */
    public Admin getCurrentAdmin() {
        try {
            String userUuid = getCurrentUserUuid();
            log.debug("🔍 Recherche admin avec UUID: {}", userUuid);
            Admin admin = adminRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new UnauthorizedAccessException("User is not an admin"));
            log.debug("✅ Admin trouvé: {}", admin.getEmail());
            return admin;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération de l'admin: {}", e.getMessage());
            throw new UnauthorizedAccessException("User is not an admin");
        }
    }

    /**
     * Récupère le Support Agent courant
     */
    public SupportAgent getCurrentSupportAgent() {
        try {
            String userUuid = getCurrentUserUuid();
            return supportAgentRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new UnauthorizedAccessException("User is not a support agent"));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du support agent: {}", e.getMessage());
            throw new UnauthorizedAccessException("User is not a support agent");
        }
    }

    /**
     * Récupère le Support Staff courant
     */
    public SupportStaff getCurrentSupportStaff() {
        try {
            String userUuid = getCurrentUserUuid();
            return supportStaffRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new UnauthorizedAccessException("User is not a support staff"));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du support staff: {}", e.getMessage());
            throw new UnauthorizedAccessException("User is not a support staff");
        }
    }

    /**
     * Récupère le Teacher Simple courant
     */
    public TeacherSimple getCurrentTeacherSimple() {
        try {
            String userUuid = getCurrentUserUuid();
            return teacherSimpleRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new UnauthorizedAccessException("User is not a teacher simple"));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du teacher simple: {}", e.getMessage());
            throw new UnauthorizedAccessException("User is not a teacher simple");
        }
    }

    /**
     * Récupère le Student Simple courant
     */
    public StudentSimple getCurrentStudentSimple() {
        try {
            String userUuid = getCurrentUserUuid();
            return studentSimpleRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new UnauthorizedAccessException("User is not a student simple"));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du student simple: {}", e.getMessage());
            throw new UnauthorizedAccessException("User is not a student simple");
        }
    }

    /**
     * Vérifie si l'utilisateur courant a un rôle spécifique
     */
    public boolean hasRole(String role) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la vérification du rôle: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Récupère le rôle de l'utilisateur courant
     */
    public String getCurrentUserRole() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getAuthorities().stream()
                        .findFirst()
                        .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                        .orElse("UNKNOWN");
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la récupération du rôle: {}", e.getMessage());
        }
        return "ADMIN";
    }

    /**
     * Récupère l'email de l'utilisateur courant
     */
    public String getCurrentUserEmail() {
        try {
            String uuid = getCurrentUserUuid();

            // Rechercher dans les différentes tables
            Optional<Admin> adminOpt = adminRepository.findByUuid(uuid);
            if (adminOpt.isPresent()) {
                return adminOpt.get().getEmail();
            }

            Optional<SupportAgent> agentOpt = supportAgentRepository.findByUuid(uuid);
            if (agentOpt.isPresent()) {
                return agentOpt.get().getEmail();
            }

            Optional<SupportStaff> staffOpt = supportStaffRepository.findByUuid(uuid);
            if (staffOpt.isPresent()) {
                return staffOpt.get().getEmail();
            }

            Optional<TeacherSimple> teacherOpt = teacherSimpleRepository.findByUuid(uuid);
            if (teacherOpt.isPresent()) {
                return teacherOpt.get().getEmail();
            }

            Optional<StudentSimple> studentOpt = studentSimpleRepository.findByUuid(uuid);
            if (studentOpt.isPresent()) {
                return studentOpt.get().getEmail();
            }

            // Fallback: chercher dans User
            /*
            Optional<User> userOpt = userRepository.findByUuid(uuid);
            if (userOpt.isPresent()) {
                return userOpt.get().getEmail();
            }

             */
        } catch (Exception e) {
            log.warn("Erreur lors de la récupération de l'email: {}", e.getMessage());
        }
        return "admin@edueasy.com";
    }

    /**
     * Vérifie si l'utilisateur est authentifié
     */
    public boolean isAuthenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null &&
                    authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getPrincipal());
        } catch (Exception e) {
            log.warn("Erreur lors de la vérification de l'authentification: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Récupère le nom complet de l'utilisateur courant
     */
    /*
    public String getCurrentUserFullName() {
        try {
            String uuid = getCurrentUserUuid();
            Optional<User> userOpt = userRepository.findByUuid(uuid);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return user.getFirstName() + " " + user.getLastName();
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la récupération du nom complet: {}", e.getMessage());
        }
        return "Unknown User";
    }

     */

    /**
     * Vérifie si l'utilisateur courant est un Admin
     */
    public boolean isAdmin() {
        return hasRole("ADMINISTRATOR") || hasRole("SUPER_ADMIN") || hasRole("ADMIN");
    }

    /**
     * Vérifie si l'utilisateur courant est un Support Agent
     */
    public boolean isSupportAgent() {
        return hasRole("SUPPORT_AGENT");
    }

    /**
     * Vérifie si l'utilisateur courant est un Teacher
     */
    public boolean isTeacher() {
        return hasRole("TEACHER_SIMPLE") || hasRole("TEACHER");
    }

    /**
     * Vérifie si l'utilisateur courant est un Student
     */
    public boolean isStudent() {
        return hasRole("STUDENT_SIMPLE") || hasRole("STUDENT");
    }
}