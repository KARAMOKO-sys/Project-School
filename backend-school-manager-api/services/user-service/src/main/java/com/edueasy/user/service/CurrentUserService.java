package com.edueasy.user.service;

import com.edueasy.common.exception.UnauthorizedAccessException;
import com.edueasy.common.model.Admin;
import com.edueasy.common.model.StudentSimple;
import com.edueasy.common.model.SupportAgent;
import com.edueasy.common.model.SupportStaff;
import com.edueasy.common.model.TeacherSimple;
import com.edueasy.user.repository.AdminRepository;
import com.edueasy.user.repository.StudentSimpleRepository;
import com.edueasy.user.repository.SupportAgentRepository;
import com.edueasy.user.repository.SupportStaffRepository;
import com.edueasy.user.repository.TeacherSimpleRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private static final Logger log = LoggerFactory.getLogger(CurrentUserService.class);
    private final AdminRepository adminRepository;
    private final SupportAgentRepository supportAgentRepository;
    private final SupportStaffRepository supportStaffRepository;
    private final TeacherSimpleRepository teacherSimpleRepository;
    private final StudentSimpleRepository studentSimpleRepository;

    public String getCurrentUserUuid() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                String uuid = authentication.getName();
                log.debug("✅ UUID extrait du token: {}", uuid);
                return uuid;
            }
        } catch (Exception e) {
            log.warn("⚠️ Impossible d'extraire l'UUID du token: {}", e.getMessage());
        }

        log.info("\ud83d\udd0d Mode développement: recherche d'un admin en base");

        try {
            Admin admin = (Admin)this.adminRepository.findByEmail("admin@edueasy.com").orElse((Admin) null);
            if (admin != null) {
                log.info("✅ Admin trouvé par email: {}", admin.getUuid());
                return admin.getUuid();
            }

            List<Admin> admins = this.adminRepository.findAll();
            if (!admins.isEmpty()) {
                log.info("✅ Premier admin trouvé: {}", ((Admin)admins.get(0)).getUuid());
                return ((Admin)admins.get(0)).getUuid();
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération de l'admin: {}", e.getMessage());
        }

        log.warn("⚠️ Aucun admin trouvé, utilisation d'un UUID par défaut");
        return "1c387650-1d84-4b5a-9986-764944c0cead";
    }

    public Admin getCurrentAdmin() {
        try {
            String userUuid = this.getCurrentUserUuid();
            log.debug("\ud83d\udd0d Recherche admin avec UUID: {}", userUuid);
            Admin admin = (Admin)this.adminRepository.findByUuid(userUuid).orElseThrow(() -> new UnauthorizedAccessException("User is not an admin"));
            log.debug("✅ Admin trouvé: {}", admin.getEmail());
            return admin;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération de l'admin: {}", e.getMessage());
            throw new UnauthorizedAccessException("User is not an admin");
        }
    }

    public SupportAgent getCurrentSupportAgent() {
        try {
            String userUuid = this.getCurrentUserUuid();
            return (SupportAgent)this.supportAgentRepository.findByUuid(userUuid).orElseThrow(() -> new UnauthorizedAccessException("User is not a support agent"));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du support agent: {}", e.getMessage());
            throw new UnauthorizedAccessException("User is not a support agent");
        }
    }

    public SupportStaff getCurrentSupportStaff() {
        try {
            String userUuid = this.getCurrentUserUuid();
            return (SupportStaff)this.supportStaffRepository.findByUuid(userUuid).orElseThrow(() -> new UnauthorizedAccessException("User is not a support staff"));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du support staff: {}", e.getMessage());
            throw new UnauthorizedAccessException("User is not a support staff");
        }
    }

    public TeacherSimple getCurrentTeacherSimple() {
        try {
            String userUuid = this.getCurrentUserUuid();
            return (TeacherSimple)this.teacherSimpleRepository.findByUuid(userUuid).orElseThrow(() -> new UnauthorizedAccessException("User is not a teacher simple"));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du teacher simple: {}", e.getMessage());
            throw new UnauthorizedAccessException("User is not a teacher simple");
        }
    }

    public StudentSimple getCurrentStudentSimple() {
        try {
            String userUuid = this.getCurrentUserUuid();
            return (StudentSimple)this.studentSimpleRepository.findByUuid(userUuid).orElseThrow(() -> new UnauthorizedAccessException("User is not a student simple"));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du student simple: {}", e.getMessage());
            throw new UnauthorizedAccessException("User is not a student simple");
        }
    }

    public boolean hasRole(String role) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getAuthorities().stream().anyMatch((authority) -> authority.getAuthority().equals("ROLE_" + role));
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la vérification du rôle: {}", e.getMessage());
        }

        return true;
    }

    public String getCurrentUserRole() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return (String)authentication.getAuthorities().stream().findFirst().map((auth) -> auth.getAuthority().replace("ROLE_", "")).orElse("UNKNOWN");
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la récupération du rôle: {}", e.getMessage());
        }

        return "ADMIN";
    }

    public String getCurrentUserEmail() {
        try {
            String uuid = this.getCurrentUserUuid();
            Admin admin = (Admin)this.adminRepository.findByUuid(uuid).orElse((Admin) null);
            if (admin != null) {
                return admin.getEmail();
            }

            SupportAgent agent = (SupportAgent)this.supportAgentRepository.findByUuid(uuid).orElse((SupportAgent) null);
            if (agent != null) {
                return agent.getEmail();
            }

            TeacherSimple teacher = (TeacherSimple)this.teacherSimpleRepository.findByUuid(uuid).orElse((TeacherSimple) null);
            if (teacher != null) {
                return teacher.getEmail();
            }

            StudentSimple student = (StudentSimple)this.studentSimpleRepository.findByUuid(uuid).orElse((StudentSimple) null);
            if (student != null) {
                return student.getEmail();
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la récupération de l'email: {}", e.getMessage());
        }

        return "admin@edueasy.com";
    }

    public boolean isAuthenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
        } catch (Exception var2) {
            return false;
        }
    }

    public CurrentUserService(final AdminRepository adminRepository, final SupportAgentRepository supportAgentRepository, final SupportStaffRepository supportStaffRepository, final TeacherSimpleRepository teacherSimpleRepository, final StudentSimpleRepository studentSimpleRepository) {
        this.adminRepository = adminRepository;
        this.supportAgentRepository = supportAgentRepository;
        this.supportStaffRepository = supportStaffRepository;
        this.teacherSimpleRepository = teacherSimpleRepository;
        this.studentSimpleRepository = studentSimpleRepository;
    }
}
