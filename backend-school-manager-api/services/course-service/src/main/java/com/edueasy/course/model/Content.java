package com.edueasy.course.model;

import com.edueasy.common.model.AuditTimestamps;
import com.edueasy.course.enums.ContentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ContentType type;

    @Column(name = "content_url")
    private String contentUrl;

    @Column(name = "duration_minutes")
    @Builder.Default
    private Integer durationMinutes = 0;

    @Column(name = "order_index")
    @Builder.Default
    private Integer orderIndex = 0;

    @Column(name = "is_free")
    @Builder.Default
    private Boolean isFree = false;

    @Column(name = "is_published")
    @Builder.Default
    private Boolean isPublished = false;

    @Column(name = "is_required")
    @Builder.Default
    private Boolean isRequired = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "course_uuid")
    private String courseUuid;

    @Column(name = "teacher_uuid")
    private String teacherUuid;

    @Embedded
    private AuditTimestamps timestamps;

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (isFree == null) {
            isFree = false;
        }
        if (isPublished == null) {
            isPublished = false;
        }
        if (isRequired == null) {
            isRequired = true;
        }
        if (durationMinutes == null) {
            durationMinutes = 0;
        }
        if (orderIndex == null) {
            orderIndex = 0;
        }
        if (timestamps == null) {
            timestamps = new AuditTimestamps();
           // timestamps.init();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (timestamps != null) {
            timestamps.setUpdatedAt(LocalDateTime.now());
        }
    }

    public boolean isComplete() {
        return title != null && !title.isEmpty()
                && type != null
                && contentUrl != null && !contentUrl.isEmpty();
    }

    public boolean isPublished() {
        return isPublished != null && isPublished;
    }

    public boolean isFree() {
        return isFree != null && isFree;
    }

    public boolean isRequired() {
        return isRequired != null && isRequired;
    }

    public String getFormattedDuration() {
        if (durationMinutes == null || durationMinutes == 0) {
            return "N/A";
        }
        if (durationMinutes < 60) {
            return durationMinutes + " min";
        }
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        if (minutes == 0) {
            return hours + " h";
        }
        return hours + "h" + minutes + "min";
    }
}