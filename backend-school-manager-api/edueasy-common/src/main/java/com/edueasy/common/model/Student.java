package com.edueasy.common.model;

import com.edueasy.common.enums.EnrollmentStatus;
import com.edueasy.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "students")
@JsonTypeName("STUDENT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Student extends User {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true, nullable = false)
    private String studentNumber;

    private String classId;

    private String level;

    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EnrollmentStatus enrollmentStatus = EnrollmentStatus.ENROLLED;

    @Embedded
    private MedicalInfo medicalInfo;

    @ElementCollection
    @CollectionTable(
            name = "student_guardians",
            joinColumns = @JoinColumn(name = "student_id")
    )
    @Builder.Default
    private List<StudentGuardian> guardians = new ArrayList<>();

    private String previousSchool;

    @Column(name = "special_needs")
    private String specialNeeds;

    @ElementCollection
    @CollectionTable(
            name = "student_enrolled_courses",
            joinColumns = @JoinColumn(name = "student_id")
    )
    @Column(name = "course_id")
    @Builder.Default
    private List<String> enrolledCourseIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "student_grades",
            joinColumns = @JoinColumn(name = "student_id")
    )
    @MapKeyColumn(name = "subject")
    @Column(name = "grade")
    @Builder.Default
    private Map<String, Double> grades = new HashMap<>();

    private Double averageScore;

    private Integer rank;

    @Column(name = "total_students")
    private Integer totalStudents;

    @Override
    public UserRole getRole() {
        return UserRole.STUDENT;
    }
}