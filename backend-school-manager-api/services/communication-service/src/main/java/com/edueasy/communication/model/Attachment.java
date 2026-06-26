package com.edueasy.communication.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "attachments",
        indexes = {
                @Index(name = "idx_attachment_file_type", columnList = "file_type"),
                @Index(name = "idx_attachment_uploaded_at", columnList = "uploaded_at")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "uploaded_by")
    private String uploadedBy;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = false;

    @Column(name = "download_count")
    @Builder.Default
    private Integer downloadCount = 0;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "checksum")
    private String checksum;

    @Column(name = "storage_path")
    private String storagePath;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
        if (downloadCount == null) {
            downloadCount = 0;
        }
        if (isPublic == null) {
            isPublic = false;
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si le fichier est une image
     */
    public boolean isImage() {
        return fileType != null && fileType.startsWith("image/");
    }

    /**
     * Vérifie si le fichier est un document PDF
     */
    public boolean isPDF() {
        return "application/pdf".equals(fileType);
    }

    /**
     * Vérifie si le fichier est un document Word
     */
    public boolean isWordDocument() {
        return fileType != null && (
                fileType.equals("application/msword") ||
                        fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        );
    }

    /**
     * Vérifie si le fichier est une feuille de calcul Excel
     */
    public boolean isExcelDocument() {
        return fileType != null && (
                fileType.equals("application/vnd.ms-excel") ||
                        fileType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        );
    }

    /**
     * Vérifie si le fichier est une présentation PowerPoint
     */
    public boolean isPowerPoint() {
        return fileType != null && (
                fileType.equals("application/vnd.ms-powerpoint") ||
                        fileType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")
        );
    }

    /**
     * Vérifie si le fichier est une vidéo
     */
    public boolean isVideo() {
        return fileType != null && fileType.startsWith("video/");
    }

    /**
     * Vérifie si le fichier est un audio
     */
    public boolean isAudio() {
        return fileType != null && fileType.startsWith("audio/");
    }

    /**
     * Vérifie si le fichier est un zip
     */
    public boolean isZip() {
        return "application/zip".equals(fileType) ||
                "application/x-zip-compressed".equals(fileType);
    }

    /**
     * Vérifie si le fichier est public
     */
    public boolean isPublic() {
        return isPublic != null && isPublic;
    }

    /**
     * Formate la taille du fichier
     */
    public String getFormattedSize() {
        if (fileSize == null) {
            return "0 B";
        }

        long bytes = fileSize;
        if (bytes < 1024) {
            return bytes + " B";
        }
        double kb = bytes / 1024.0;
        if (kb < 1024) {
            return String.format("%.1f KB", kb);
        }
        double mb = kb / 1024.0;
        if (mb < 1024) {
            return String.format("%.1f MB", mb);
        }
        double gb = mb / 1024.0;
        return String.format("%.1f GB", gb);
    }

    /**
     * Incrémente le compteur de téléchargements
     */
    public void incrementDownloadCount() {
        if (downloadCount == null) {
            downloadCount = 0;
        }
        downloadCount++;
    }

    /**
     * Vérifie si le fichier a une description
     */
    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    /**
     * Vérifie si le fichier a un checksum
     */
    public boolean hasChecksum() {
        return checksum != null && !checksum.isEmpty();
    }

    /**
     * Vérifie si le fichier est valide (taille > 0)
     */
    public boolean isValid() {
        return fileSize != null && fileSize > 0
                && fileName != null && !fileName.isEmpty()
                && fileUrl != null && !fileUrl.isEmpty();
    }

    /**
     * Retourne l'extension du fichier
     */
    public String getFileExtension() {
        if (fileName == null) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Vérifie si le fichier est dans un format autorisé
     */
    public boolean isAllowedFormat(List<String> allowedFormats) {
        if (allowedFormats == null || allowedFormats.isEmpty()) {
            return true;
        }
        String extension = getFileExtension();
        return allowedFormats.stream()
                .anyMatch(format -> format.equalsIgnoreCase(extension));
    }

    /**
     * Vérifie si la taille du fichier est dans les limites
     */
    public boolean isWithinSizeLimit(long maxSize) {
        return fileSize != null && fileSize <= maxSize;
    }

    /**
     * Retourne le nom du fichier sans extension
     */
    public String getFileNameWithoutExtension() {
        if (fileName == null) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    /**
     * Met à jour les métadonnées du fichier
     */
    public void updateMetadata(String fileName, String fileType, Long fileSize) {
        if (fileName != null && !fileName.isEmpty()) {
            this.fileName = fileName;
        }
        if (fileType != null && !fileType.isEmpty()) {
            this.fileType = fileType;
        }
        if (fileSize != null && fileSize > 0) {
            this.fileSize = fileSize;
        }
    }

    /**
     * Rendre le fichier public
     */
    public void makePublic() {
        this.isPublic = true;
    }

    /**
     * Rendre le fichier privé
     */
    public void makePrivate() {
        this.isPublic = false;
    }
}