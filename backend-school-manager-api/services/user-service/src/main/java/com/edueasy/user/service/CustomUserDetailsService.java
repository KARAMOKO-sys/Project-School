package com.edueasy.user.service;

import com.edueasy.common.model.Admin;
import com.edueasy.common.model.SupportAgent;
import com.edueasy.common.model.TeacherSimple;
import com.edueasy.user.repository.AdminRepository;
import com.edueasy.user.repository.SupportAgentRepository;
import com.edueasy.user.repository.TeacherSimpleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final AdminRepository adminRepository;
    private final SupportAgentRepository supportAgentRepository;
    private final TeacherSimpleRepository teacherSimpleRepository;

    public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        log.debug("Loading user by UUID: {}", uuid);
        Admin admin = (Admin)this.adminRepository.findByUuid(uuid).orElse((Admin) null);
        if (admin != null) {
            log.debug("Found admin with UUID: {}", uuid);
            String role = "ROLE_ADMINISTRATOR";
            return User.builder().username(admin.getUuid()).password(admin.getPasswordHash()).authorities(new GrantedAuthority[]{new SimpleGrantedAuthority(role)}).build();
        } else {
            SupportAgent supportAgent = (SupportAgent)this.supportAgentRepository.findByUuid(uuid).orElse((SupportAgent) null);
            if (supportAgent != null) {
                log.debug("Found support agent with UUID: {}", uuid);
                String role = "ROLE_SUPPORT_AGENT";
                return User.builder().username(supportAgent.getUuid()).password(supportAgent.getPasswordHash()).authorities(new GrantedAuthority[]{new SimpleGrantedAuthority(role)}).build();
            } else {
                TeacherSimple teacher = (TeacherSimple)this.teacherSimpleRepository.findByUuid(uuid).orElse((TeacherSimple) null);
                if (teacher != null) {
                    log.debug("Found teacher with UUID: {}", uuid);
                    String role = "ROLE_TEACHER_SIMPLE";
                    return User.builder().username(teacher.getUuid()).password(teacher.getPasswordHash()).authorities(new GrantedAuthority[]{new SimpleGrantedAuthority(role)}).build();
                } else {
                    log.error("User not found with UUID: {}", uuid);
                    throw new UsernameNotFoundException("User not found with UUID: " + uuid);
                }
            }
        }
    }

    public CustomUserDetailsService(final AdminRepository adminRepository, final SupportAgentRepository supportAgentRepository, final TeacherSimpleRepository teacherSimpleRepository) {
        this.adminRepository = adminRepository;
        this.supportAgentRepository = supportAgentRepository;
        this.teacherSimpleRepository = teacherSimpleRepository;
    }
}
