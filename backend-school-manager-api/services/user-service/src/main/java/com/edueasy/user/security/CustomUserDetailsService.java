/*
package com.edueasy.user.security;

import com.edueasy.common.model.User;
import com.edueasy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);

        // Essayer de trouver par email
        User user = userRepository.findByEmail(username).orElse(null);

        // Si pas trouvé par email, essayer par UUID
        if (user == null) {
            user = userRepository.findByUuid(username).orElse(null);
        }

        // Si toujours pas trouvé, essayer par username
        if (user == null) {
            user = userRepository.findByUsername(username).orElse(null);
        }

        if (user == null) {
            log.warn("User not found with identifier: {}", username);
            throw new UsernameNotFoundException("User not found with identifier: " + username);
        }

        log.debug("User found: {}", user.getEmail());
        return user;
    }
}

 */