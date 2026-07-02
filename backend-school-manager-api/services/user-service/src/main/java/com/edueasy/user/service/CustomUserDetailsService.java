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

    public CustomUserDetailsService(AdminRepository adminRepository,
                                    SupportAgentRepository supportAgentRepository,
                                    TeacherSimpleRepository teacherSimpleRepository) {
        this.adminRepository = adminRepository;
        this.supportAgentRepository = supportAgentRepository;
        this.teacherSimpleRepository = teacherSimpleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        log.debug("Loading user by UUID: {}", uuid);

        Admin admin = adminRepository.findByUuid(uuid).orElse(null);
        if (admin != null) {
            log.debug("Found admin with UUID: {}", uuid);
            return User.builder()
                    .username(admin.getUuid())
                    .password(admin.getPasswordHash())
                    .authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))
                    .build();
        }

        SupportAgent supportAgent = supportAgentRepository.findByUuid(uuid).orElse(null);
        if (supportAgent != null) {
            log.debug("Found support agent with UUID: {}", uuid);
            return User.builder()
                    .username(supportAgent.getUuid())
                    .password(supportAgent.getPasswordHash())
                    .authorities(new SimpleGrantedAuthority("ROLE_SUPPORT_AGENT"))
                    .build();
        }

        TeacherSimple teacher = teacherSimpleRepository.findByUuid(uuid).orElse(null);
        if (teacher != null) {
            log.debug("Found teacher with UUID: {}", uuid);
            return User.builder()
                    .username(teacher.getUuid())
                    .password(teacher.getPasswordHash())
                    .authorities(new SimpleGrantedAuthority("ROLE_TEACHER_SIMPLE"))
                    .build();
        }

        log.error("User not found with UUID: {}", uuid);
        throw new UsernameNotFoundException("User not found with UUID: " + uuid);
    }
}