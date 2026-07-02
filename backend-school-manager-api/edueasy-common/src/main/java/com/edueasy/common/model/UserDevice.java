package com.edueasy.common.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_devices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_device_id",
                        columnNames = {"user_uuid", "device_id"}
                )
        },
        indexes = {
                @Index(name = "idx_user_device_user_uuid", columnList = "user_uuid"),
                @Index(name = "idx_user_device_device_id", columnList = "device_id"),
                @Index(name = "idx_user_device_last_used", columnList = "last_used_at")
        }
)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserDevice extends AuditTimestamps {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    // 🔥 Remplacer la relation ManyToOne par un simple champ UUID
    @Column(name = "user_uuid", nullable = false, length = 36)
    private String userUuid;

    @Column(name = "device_id", nullable = false, length = 255)
    private String deviceId;

    @Column(name = "device_name", length = 255)
    private String deviceName;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "device_model", length = 100)
    private String deviceModel;

    @Column(name = "os_version", length = 50)
    private String osVersion;

    @Column(name = "app_version", length = 50)
    private String appVersion;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;

    @Column(name = "push_token", length = 255)
    private String pushToken;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "is_trusted")
    @Builder.Default
    private boolean isTrusted = false;

    @Column(name = "is_verified")
    @Builder.Default
    private boolean isVerified = false;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "is_compromised")
    @Builder.Default
    private boolean isCompromised = false;

    @Column(name = "compromised_at")
    private LocalDateTime compromisedAt;

    @Column(name = "compromise_details", length = 500)
    private String compromiseDetails;

    // ===== Méthodes de cycle de vie =====

    /*
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (lastUsedAt == null) {
            lastUsedAt = LocalDateTime.now();
        }
        if (userUuid == null) {
            throw new IllegalStateException("L'UUID de l'utilisateur ne peut pas être null");
        }
    }

        @PreUpdate
    protected void onUpdate() {
        super.onUpdate();
        if (lastUsedAt == null) {
            lastUsedAt = LocalDateTime.now();
        }
    }


     */


    // ===== Méthodes métier =====

    /**
     * Associe l'appareil à un utilisateur par son UUID
     */
    public void associateWithUser(String userUuid) {
        if (userUuid != null && !userUuid.isEmpty()) {
            this.userUuid = userUuid;
        } else {
            throw new IllegalArgumentException("L'UUID de l'utilisateur ne peut pas être null ou vide");
        }
    }

    /**
     * Vérifie si l'appareil est associé à un utilisateur
     */
    public boolean hasUser() {
        return userUuid != null && !userUuid.isEmpty();
    }

    /**
     * Récupère l'UUID de l'utilisateur associé
     */
    public String getUserUuid() {
        return userUuid;
    }

    /**
     * Met à jour la date de dernière utilisation
     */
    public void updateLastUsed() {
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * Active l'appareil
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Désactive l'appareil
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Vérifie si l'appareil est actif
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Marque l'appareil comme compromis
     */
    public void markAsCompromised(String details) {
        this.isCompromised = true;
        this.compromisedAt = LocalDateTime.now();
        this.compromiseDetails = details;
        this.isTrusted = false;
        this.isActive = false;
    }

    /**
     * Marque l'appareil comme non compromis
     */
    public void unmarkAsCompromised() {
        this.isCompromised = false;
        this.compromisedAt = null;
        this.compromiseDetails = null;
    }

    /**
     * Vérifie si l'appareil peut être utilisé
     */
    public boolean canBeUsed() {
        return isActive && !isCompromised && hasUser();
    }

    /**
     * Marque l'appareil comme vérifié
     */
    public void verify() {
        this.isVerified = true;
    }

    /**
     * Marque l'appareil comme non vérifié
     */
    public void unverify() {
        this.isVerified = false;
    }

    /**
     * Marque l'appareil comme approuvé
     */
    public void trust() {
        this.isTrusted = true;
        if (!this.isVerified) {
            this.verify();
        }
    }

    /**
     * Marque l'appareil comme non approuvé
     */
    public void untrust() {
        this.isTrusted = false;
    }

    /**
     * Met à jour le token FCM
     */
    public void updateFcmToken(String newToken) {
        this.fcmToken = newToken;
    }

    /**
     * Supprime le token FCM
     */
    public void removeFcmToken() {
        this.fcmToken = null;
    }

    /**
     * Vérifie si l'appareil a un token FCM
     */
    public boolean hasFcmToken() {
        return fcmToken != null && !fcmToken.isEmpty();
    }

    /**
     * Met à jour le token push
     */
    public void updatePushToken(String newToken) {
        this.pushToken = newToken;
    }

    /**
     * Supprime le token push
     */
    public void removePushToken() {
        this.pushToken = null;
    }

    /**
     * Vérifie si l'appareil a un token push
     */
    public boolean hasPushToken() {
        return pushToken != null && !pushToken.isEmpty();
    }

    /**
     * Met à jour la localisation de l'appareil
     */
    public void updateLocation(String ipAddress, String location, String userAgent) {
        this.ipAddress = ipAddress;
        this.location = location;
        this.userAgent = userAgent;
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * Obtient le nom complet de l'appareil
     */
    public String getFullDeviceName() {
        StringBuilder name = new StringBuilder();
        if (deviceName != null && !deviceName.isEmpty()) {
            name.append(deviceName);
        }
        if (deviceModel != null && !deviceModel.isEmpty()) {
            if (!name.isEmpty()) {
                name.append(" - ");
            }
            name.append(deviceModel);
        }
        if (deviceType != null && !deviceType.isEmpty()) {
            if (!name.isEmpty()) {
                name.append(" (");
                name.append(deviceType);
                name.append(")");
            }
        }
        if (name.isEmpty()) {
            return "Appareil inconnu";
        }
        return name.toString();
    }

    /**
     * Vérifie si l'appareil est un mobile
     */
    public boolean isMobile() {
        return deviceType != null &&
                (deviceType.toLowerCase().contains("android") ||
                        deviceType.toLowerCase().contains("ios") ||
                        deviceType.toLowerCase().contains("mobile") ||
                        deviceType.toLowerCase().contains("phone"));
    }

    /**
     * Vérifie si l'appareil est un desktop
     */
    public boolean isDesktop() {
        return deviceType != null &&
                (deviceType.toLowerCase().contains("windows") ||
                        deviceType.toLowerCase().contains("mac") ||
                        deviceType.toLowerCase().contains("linux") ||
                        deviceType.toLowerCase().contains("desktop"));
    }

    /**
     * Vérifie si l'appareil est un navigateur
     */
    public boolean isBrowser() {
        return deviceType != null &&
                (deviceType.toLowerCase().contains("chrome") ||
                        deviceType.toLowerCase().contains("firefox") ||
                        deviceType.toLowerCase().contains("safari") ||
                        deviceType.toLowerCase().contains("edge") ||
                        deviceType.toLowerCase().contains("browser"));
    }

    /**
     * Obtient le statut de l'appareil
     */
    public String getStatus() {
        if (!isActive) {
            return "Désactivé";
        }
        if (isCompromised) {
            return "Compromis";
        }
        if (!isVerified) {
            return "Non vérifié";
        }
        if (!isTrusted) {
            return "Non approuvé";
        }
        return "Actif";
    }

    /**
     * Obtient la couleur du statut pour l'UI
     */
    public String getStatusColor() {
        if (!isActive) {
            return "gray";
        }
        if (isCompromised) {
            return "red";
        }
        if (!isVerified) {
            return "orange";
        }
        if (!isTrusted) {
            return "yellow";
        }
        return "green";
    }

    /**
     * Vérifie si l'appareil est en ligne (utilisé récemment)
     */
    public boolean isOnline() {
        // Si la dernière utilisation remonte à moins de 5 minutes
        return lastUsedAt != null &&
                lastUsedAt.isAfter(LocalDateTime.now().minusMinutes(5));
    }

    /**
     * Obtient le temps depuis la dernière utilisation
     */
    public String getLastUsedAgo() {
        if (lastUsedAt == null) {
            return "Jamais";
        }
        java.time.Duration duration = java.time.Duration.between(lastUsedAt, LocalDateTime.now());
        if (duration.getSeconds() < 60) {
            return "À l'instant";
        } else if (duration.getSeconds() < 3600) {
            return duration.getSeconds() / 60 + " min";
        } else if (duration.getSeconds() < 86400) {
            return duration.getSeconds() / 3600 + " h";
        } else {
            return duration.getSeconds() / 86400 + " j";
        }
    }

    /**
     * Vérifie si l'appareil est récent (utilisé dans les 24h)
     */
    public boolean isRecentlyUsed() {
        return lastUsedAt != null &&
                lastUsedAt.isAfter(LocalDateTime.now().minusHours(24));
    }

    // ===== Méthodes de factory =====

    /**
     * Crée un appareil pour un utilisateur
     */
    public static UserDevice createForUser(String userUuid, String deviceId, String deviceType) {
        if (userUuid == null || userUuid.isEmpty()) {
            throw new IllegalArgumentException("L'UUID de l'utilisateur ne peut pas être null ou vide");
        }
        return UserDevice.builder()
                .userUuid(userUuid)
                .deviceId(deviceId)
                .deviceType(deviceType)
                .isActive(true)
                .isTrusted(false)
                .isVerified(false)
                .isCompromised(false)
                .lastUsedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Crée un appareil mobile pour un utilisateur
     */
    public static UserDevice createMobileDevice(String userUuid, String deviceId, String deviceModel, String osVersion) {
        UserDevice device = createForUser(userUuid, deviceId, "Mobile");
        device.setDeviceModel(deviceModel);
        device.setOsVersion(osVersion);
        return device;
    }

    /**
     * Crée un appareil desktop pour un utilisateur
     */
    public static UserDevice createDesktopDevice(String userUuid, String deviceId, String deviceName, String osVersion) {
        UserDevice device = createForUser(userUuid, deviceId, "Desktop");
        device.setDeviceName(deviceName);
        device.setOsVersion(osVersion);
        return device;
    }

    /**
     * Crée un navigateur pour un utilisateur
     */
    public static UserDevice createBrowserDevice(String userUuid, String deviceId, String browserName) {
        UserDevice device = createForUser(userUuid, deviceId, "Browser");
        device.setDeviceName(browserName);
        return device;
    }

    /**
     * Crée un appareil avec token FCM
     */
    public static UserDevice createWithFcmToken(String userUuid, String deviceId, String deviceType, String fcmToken) {
        UserDevice device = createForUser(userUuid, deviceId, deviceType);
        device.setFcmToken(fcmToken);
        return device;
    }

    /**
     * Crée un appareil avec token push
     */
    public static UserDevice createWithPushToken(String userUuid, String deviceId, String deviceType, String pushToken) {
        UserDevice device = createForUser(userUuid, deviceId, deviceType);
        device.setPushToken(pushToken);
        return device;
    }

    /**
     * Crée un appareil avec tous les détails
     */
    public static UserDevice createFull(
            String userUuid,
            String deviceId,
            String deviceName,
            String deviceType,
            String deviceModel,
            String osVersion,
            String appVersion,
            String fcmToken) {
        UserDevice device = createForUser(userUuid, deviceId, deviceType);
        device.setDeviceName(deviceName);
        device.setDeviceModel(deviceModel);
        device.setOsVersion(osVersion);
        device.setAppVersion(appVersion);
        device.setFcmToken(fcmToken);
        return device;
    }
}