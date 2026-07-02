package com.edueasy.course.service;

import com.edueasy.course.client.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserServiceClient userServiceClient;

    public CustomUserDetailsService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);

        try {
            // Essayer de récupérer le teacher par UUID
            var teacher = userServiceClient.getTeacherByUuid(username);
            if (teacher != null && teacher.getUuid() != null) {
                log.debug("Found teacher with UUID: {}", username);
                return new User(
                        teacher.getUuid(),
                        "",
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER_SIMPLE"))
                );
            }
        } catch (Exception e) {
            log.debug("Teacher not found with UUID: {}", username);
        }

        // Si l'utilisateur n'est pas trouvé, créer un UserDetails par défaut
        // avec le username fourni (pour permettre au filtre JWT de continuer)
        log.warn("User not found with identifier: {}, creating default user with this ID", username);
        return new User(
                username,  // Utiliser l'UUID comme username
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}