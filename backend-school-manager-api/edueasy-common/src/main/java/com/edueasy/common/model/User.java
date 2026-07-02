// User.java - DEVIENT UNE CLASSE ABSTRAITE SANS TABLE
package com.edueasy.common.model;

import com.edueasy.common.enums.UserRole;
import com.edueasy.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@MappedSuperclass  // 🔥 PAS DE TABLE EN BASE DE DONNÉES
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class User extends AuditTimestamps implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @Column(unique = true, nullable = false, updatable = false, length = 36)
    private String uuid;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "locale", length = 20)
    @Builder.Default
    private String locale = "fr";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "email_verified")
    @Builder.Default
    private boolean emailVerified = false;

    @Column(name = "phone_verified")
    @Builder.Default
    private boolean phoneVerified = false;

    @Column(name = "two_factor_enabled")
    @Builder.Default
    private boolean twoFactorEnabled = false;

    @Column(name = "account_non_locked")
    @Builder.Default
    private boolean accountNonLocked = true;

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private int failedLoginAttempts = 0;

    @Column(name = "lockout_time")
    private LocalDateTime lockoutTime;

    @Column(name = "preferred_language")
    @Builder.Default
    private String preferredLanguage = "fr";

    @Column(name = "timezone")
    @Builder.Default
    private String timezone = "UTC";

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        initializeAuditFields();
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (status == null) {
            status = UserStatus.PENDING;
        }
        if (locale == null) {
            locale = "fr";
        }
        if (preferredLanguage == null) {
            preferredLanguage = "fr";
        }
        if (timezone == null) {
            timezone = "UTC";
        }
    }

    // ===== Méthodes UserDetails =====

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
        if (this.role.getPermissions() != null) {
            authorities.addAll(
                    this.role.getPermissions()
                            .stream()
                            .map(permission -> new SimpleGrantedAuthority(permission.name()))
                            .collect(Collectors.toSet())
            );
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status == UserStatus.ACTIVE;
    }

    // ===== Méthodes métier =====

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.accountNonLocked = false;
            this.lockoutTime = LocalDateTime.now().plusMinutes(30);
        }
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.accountNonLocked = true;
        this.lockoutTime = null;
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public boolean isLocked() {
        if (!this.accountNonLocked && this.lockoutTime != null) {
            if (LocalDateTime.now().isAfter(this.lockoutTime)) {
                this.resetFailedLoginAttempts();
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean hasUuid() {
        return uuid != null && !uuid.isEmpty();
    }

    public boolean hasAddress() {
        return address != null;
    }

    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.isEmpty();
    }

    public boolean hasProfilePicture() {
        return profilePictureUrl != null && !profilePictureUrl.isEmpty();
    }

    public boolean isAdmin() {
        return role != null && role.isAdminRole();
    }

    public boolean isTeacher() {
        return role != null && role.isTeacherRole();
    }

    public boolean isStudent() {
        return role != null && role.isStudentRole();
    }

    public boolean isParentOrGuardian() {
        return role != null && role.isParentOrGuardian();
    }

    public boolean isSupportRole() {
        return role != null && role.isSupportRole();
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void enableTwoFactor() {
        this.twoFactorEnabled = true;
    }

    public void disableTwoFactor() {
        this.twoFactorEnabled = false;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void updateStatus(UserStatus newStatus) {
        if (newStatus != null) {
            this.status = newStatus;
        }
    }

    public boolean canBeActivated() {
        return status == UserStatus.PENDING || status == UserStatus.INACTIVE;
    }

    public boolean canBeDeactivated() {
        return status == UserStatus.ACTIVE;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }

    public void ban() {
        this.status = UserStatus.BANNED;
    }

    public void delete() {
        this.status = UserStatus.DELETED;
        super.markAsDeleted(this.username);
    }

    public void archive() {
        this.status = UserStatus.ARCHIVED;
    }

    public void restore() {
        this.status = UserStatus.ACTIVE;
        super.restore();
    }

    public boolean hasRole(UserRole role) {
        return this.role == role;
    }

    public boolean hasPermission(String permission) {
        if (role == null || role.getPermissions() == null) {
            return false;
        }
        return role.getPermissions().stream()
                .anyMatch(p -> p.name().equals(permission));
    }

    public String getStatusLabel() {
        if (status == null) {
            return "Non défini";
        }
        return switch (status) {
            case ACTIVE -> "Actif";
            case INACTIVE -> "Inactif";
            case PENDING -> "En attente";
            case SUSPENDED -> "Suspendu";
            case BANNED -> "Banni";
            case DELETED -> "Supprimé";
            case ARCHIVED -> "Archivé";
        };
    }

    public boolean canLogin() {
        return status == UserStatus.ACTIVE || status == UserStatus.PENDING;
    }

    public String getAuthIdentifier() {
        return email != null ? email : username;
    }

    // 🔥 Ajouter une méthode abstraite pour le rôle
    public abstract UserRole getRole();
}