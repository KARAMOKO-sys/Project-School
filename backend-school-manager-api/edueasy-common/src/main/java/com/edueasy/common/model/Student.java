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

    // 🔥 Remplacer la relation OneToOne par un simple champ UUID
    @Column(name = "user_uuid", length = 36)
    private String userUuid;

    @Column(unique = true, nullable = false)
    private String studentNumber;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "level")
    private String level;

    @Column(name = "enrollment_date")
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

    @Column(name = "previous_school")
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

    @Column(name = "average_score")
    private Double averageScore;

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "total_students")
    private Integer totalStudents;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (studentNumber == null) {
            generateStudentNumber();
        }
        if (enrollmentStatus == null) {
            enrollmentStatus = EnrollmentStatus.ENROLLED;
        }
        if (enrollmentDate == null) {
            enrollmentDate = LocalDate.now();
        }
        // Par défaut, l'utilisateur associé est l'étudiant lui-même
        if (userUuid == null && getUuid() != null) {
            this.userUuid = getUuid();
        }
    }

    // ===== Méthodes métier =====

    @Override
    public UserRole getRole() {
        return UserRole.STUDENT;
    }

    /**
     * Génère un numéro d'étudiant unique
     */
    public void generateStudentNumber() {
        if (studentNumber == null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            this.studentNumber = "STU-" + timestamp + "-" + random;
        }
    }

    /**
     * Obtient le nom complet de l'étudiant
     */
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * Vérifie si l'étudiant a un numéro d'étudiant
     */
    public boolean hasStudentNumber() {
        return studentNumber != null && !studentNumber.isEmpty();
    }

    /**
     * Vérifie si l'étudiant est actif
     */
    public boolean isActive() {
        return enrollmentStatus == EnrollmentStatus.ENROLLED
                || enrollmentStatus == EnrollmentStatus.ACTIVE;
    }

    /**
     * Vérifie si l'étudiant est diplômé
     */
    public boolean isGraduated() {
        return enrollmentStatus == EnrollmentStatus.GRADUATED;
    }

    /**
     * Vérifie si l'étudiant est suspendu
     */
    public boolean isSuspended() {
        return enrollmentStatus == EnrollmentStatus.SUSPENDED;
    }

    /**
     * Vérifie si l'étudiant a abandonné
     */
    public boolean isDropped() {
        return enrollmentStatus == EnrollmentStatus.DROPPED;
    }

    /**
     * Vérifie si l'étudiant est inscrit
     */
    public boolean isEnrolled() {
        return enrollmentStatus == EnrollmentStatus.ENROLLED;
    }

    /**
     * Vérifie si l'étudiant est en congé
     */
    public boolean isOnLeave() {
        return enrollmentStatus == EnrollmentStatus.ON_LEAVE;
    }

    /**
     * Vérifie si l'étudiant est en attente
     */
    public boolean isPending() {
        return enrollmentStatus == EnrollmentStatus.PENDING;
    }

    /**
     * Vérifie si l'étudiant est transféré
     */
    public boolean isTransferred() {
        return enrollmentStatus == EnrollmentStatus.TRANSFERRED;
    }

    /**
     * Diplôme l'étudiant
     */
    public void graduate() {
        this.enrollmentStatus = EnrollmentStatus.GRADUATED;
    }

    /**
     * Suspend l'étudiant
     */
    public void suspend() {
        this.enrollmentStatus = EnrollmentStatus.SUSPENDED;
    }

    /**
     * Active l'étudiant
     */
    public void activate() {
        this.enrollmentStatus = EnrollmentStatus.ACTIVE;
    }

    /**
     * Met l'étudiant en congé
     */
    public void setOnLeave() {
        this.enrollmentStatus = EnrollmentStatus.ON_LEAVE;
    }

    /**
     * Inscrit l'étudiant
     */
    public void enroll() {
        this.enrollmentStatus = EnrollmentStatus.ENROLLED;
        if (enrollmentDate == null) {
            enrollmentDate = LocalDate.now();
        }
    }

    /**
     * Marque l'étudiant comme abandonné
     */
    public void drop() {
        this.enrollmentStatus = EnrollmentStatus.DROPPED;
    }

    /**
     * Ajoute un tuteur à l'étudiant
     */
    public void addGuardian(StudentGuardian guardian) {
        if (guardians == null) {
            guardians = new ArrayList<>();
        }
        if (!guardians.contains(guardian)) {
            guardians.add(guardian);
        }
    }

    /**
     * Supprime un tuteur de l'étudiant
     */
    public void removeGuardian(StudentGuardian guardian) {
        if (guardians != null) {
            guardians.remove(guardian);
        }
    }

    /**
     * Obtient les tuteurs de l'étudiant
     */
    public List<StudentGuardian> getGuardians() {
        if (guardians == null) {
            guardians = new ArrayList<>();
        }
        return guardians;
    }

    /**
     * Vérifie si l'étudiant a des tuteurs
     */
    public boolean hasGuardians() {
        return guardians != null && !guardians.isEmpty();
    }

    /**
     * Ajoute un cours à l'étudiant
     */
    public void addCourse(String courseId) {
        if (enrolledCourseIds == null) {
            enrolledCourseIds = new ArrayList<>();
        }
        if (!enrolledCourseIds.contains(courseId)) {
            enrolledCourseIds.add(courseId);
        }
    }

    /**
     * Supprime un cours de l'étudiant
     */
    public void removeCourse(String courseId) {
        if (enrolledCourseIds != null) {
            enrolledCourseIds.remove(courseId);
        }
    }

    /**
     * Vérifie si l'étudiant est inscrit à un cours
     */
    public boolean isEnrolledInCourse(String courseId) {
        return enrolledCourseIds != null && enrolledCourseIds.contains(courseId);
    }

    /**
     * Obtient le nombre de cours de l'étudiant
     */
    public int getCourseCount() {
        return enrolledCourseIds != null ? enrolledCourseIds.size() : 0;
    }

    /**
     * Vérifie si l'étudiant a des cours
     */
    public boolean hasCourses() {
        return enrolledCourseIds != null && !enrolledCourseIds.isEmpty();
    }

    /**
     * Ajoute une note pour un sujet
     */
    public void addGrade(String subject, Double grade) {
        if (grades == null) {
            grades = new HashMap<>();
        }
        grades.put(subject, grade);
        updateAverageScore();
    }

    /**
     * Supprime une note pour un sujet
     */
    public void removeGrade(String subject) {
        if (grades != null) {
            grades.remove(subject);
            updateAverageScore();
        }
    }

    /**
     * Obtient la note pour un sujet
     */
    public Double getGrade(String subject) {
        return grades != null ? grades.get(subject) : null;
    }

    /**
     * Vérifie si l'étudiant a des notes
     */
    public boolean hasGrades() {
        return grades != null && !grades.isEmpty();
    }

    /**
     * Obtient le nombre de notes
     */
    public int getGradeCount() {
        return grades != null ? grades.size() : 0;
    }

    /**
     * Met à jour la moyenne générale
     */
    private void updateAverageScore() {
        if (grades != null && !grades.isEmpty()) {
            double sum = grades.values().stream().mapToDouble(Double::doubleValue).sum();
            this.averageScore = sum / grades.size();
        } else {
            this.averageScore = null;
        }
    }

    /**
     * Calcule la moyenne générale
     */
    public Double calculateAverage() {
        if (grades == null || grades.isEmpty()) {
            return null;
        }
        double sum = grades.values().stream().mapToDouble(Double::doubleValue).sum();
        return sum / grades.size();
    }

    /**
     * Obtient l'étiquette du statut d'inscription
     */
    public String getEnrollmentStatusLabel() {
        if (enrollmentStatus == null) {
            return "Non défini";
        }
        return switch (enrollmentStatus) {
            case PENDING -> "En attente";
            case ENROLLED -> "Inscrit";
            case ACTIVE -> "Actif";
           // case INACTIVE -> "Inactif";
            case SUSPENDED -> "Suspendu";
            case DROPPED -> "Abandonné";
            case WITHDRAWN -> null;
            case GRADUATED -> "Diplômé";
            case EXPELLED -> null;
            case DROPPED_OUT -> null;
            case ON_LEAVE -> "En congé";
            case TRANSFERRED -> "Transféré";
            case DEFERRED -> null;
            case IN_PROGRESS -> null;
            case COMPLETED -> null;
            case CANCELLED -> null;
        };
    }

    /**
     * Obtient la couleur du statut d'inscription pour l'UI
     */
    public String getEnrollmentStatusColor() {
        if (enrollmentStatus == null) {
            return "gray";
        }
        return switch (enrollmentStatus) {
            case PENDING -> "orange";
            case ENROLLED -> "blue";
            case ACTIVE -> "green";
          //  case INACTIVE -> "gray";
            case SUSPENDED -> "red";
            case DROPPED -> "darkred";
            case WITHDRAWN -> null;
            case GRADUATED -> "purple";
            case EXPELLED -> null;
            case DROPPED_OUT -> null;
            case ON_LEAVE -> "yellow";
            case TRANSFERRED -> "cyan";
            case DEFERRED -> null;
            case IN_PROGRESS -> null;
            case COMPLETED -> null;
            case CANCELLED -> null;
        };
    }

    /**
     * Vérifie si l'étudiant peut être promu
     */
    public boolean canBePromoted() {
        return isActive()
                && enrollmentStatus != EnrollmentStatus.GRADUATED
                && level != null;
    }

    /**
     * Obtient les informations académiques de l'étudiant
     */
    public String getAcademicInfo() {
        StringBuilder info = new StringBuilder();
        info.append(getFullName());
        if (studentNumber != null) {
            info.append(" (").append(studentNumber).append(")");
        }
        if (level != null) {
            info.append(" - Niveau: ").append(level);
        }
        if (classId != null) {
            info.append(" - Classe: ").append(classId);
        }
        if (enrollmentStatus != null) {
            info.append(" - ").append(getEnrollmentStatusLabel());
        }
        return info.toString();
    }

    /**
     * Obtient les informations médicales de l'étudiant
     */
    public MedicalInfo getMedicalInfo() {
        if (medicalInfo == null) {
            medicalInfo = new MedicalInfo();
        }
        return medicalInfo;
    }

    /**
     * Vérifie si l'étudiant a des besoins spéciaux
     */
    public boolean hasSpecialNeeds() {
        return specialNeeds != null && !specialNeeds.isEmpty();
    }

    // ===== Méthodes pour la gestion de l'UUID =====

    /**
     * Associe l'étudiant à un utilisateur par son UUID
     */
    public void associateWithUser(String userUuid) {
        if (userUuid != null && !userUuid.isEmpty()) {
            this.userUuid = userUuid;
        } else {
            throw new IllegalArgumentException("L'UUID de l'utilisateur ne peut pas être null ou vide");
        }
    }

    /**
     * Associe l'étudiant à lui-même
     */
    public void associateWithSelf() {
        this.userUuid = getUuid();
    }

    /**
     * Vérifie si l'étudiant est associé à un utilisateur
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
     * Dissocie l'étudiant de l'utilisateur
     */
    public void disassociateFromUser() {
        this.userUuid = null;
    }

    /**
     * Vérifie si l'étudiant est associé à lui-même
     */
    public boolean isAssociatedWithSelf() {
        return userUuid != null && userUuid.equals(getUuid());
    }

    // ===== Méthodes de factory =====

    /**
     * Crée un étudiant avec les informations de base
     */
    public static Student createBasic(String firstName, String lastName, String email, String username) {
        Student student = Student.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .username(username)
                .enrollmentStatus(EnrollmentStatus.ENROLLED)
                .enrollmentDate(LocalDate.now())
                .build();
        student.generateStudentNumber();
        student.associateWithSelf();
        return student;
    }

    /**
     * Crée un étudiant avec un niveau spécifique
     */
    public static Student createWithLevel(String firstName, String lastName, String email, String username, String level) {
        Student student = createBasic(firstName, lastName, email, username);
        student.setLevel(level);
        return student;
    }

    /**
     * Crée un étudiant avec une classe spécifique
     */
    public static Student createWithClass(String firstName, String lastName, String email, String username, String classId) {
        Student student = createBasic(firstName, lastName, email, username);
        student.setClassId(classId);
        return student;
    }

    /**
     * Crée un étudiant avec un tuteur
     */
    public static Student createWithGuardian(String firstName, String lastName, String email, String username, StudentGuardian guardian) {
        Student student = createBasic(firstName, lastName, email, username);
        student.addGuardian(guardian);
        return student;
    }

    /**
     * Crée un étudiant avec des besoins spéciaux
     */
    public static Student createWithSpecialNeeds(String firstName, String lastName, String email, String username, String specialNeeds) {
        Student student = createBasic(firstName, lastName, email, username);
        student.setSpecialNeeds(specialNeeds);
        return student;
    }

    // ===== Méthodes pour le classement =====

    /**
     * Met à jour le classement de l'étudiant
     */
    public void updateRank(int rank, int totalStudents) {
        this.rank = rank;
        this.totalStudents = totalStudents;
    }

    /**
     * Obtient le classement en pourcentage
     */
    public double getRankPercentage() {
        if (rank == null || totalStudents == null || totalStudents == 0) {
            return 0;
        }
        return ((double) rank / totalStudents) * 100;
    }

    /**
     * Obtient le rang en texte
     */
    public String getRankText() {
        if (rank == null) {
            return "Non classé";
        }
        return switch (rank) {
            case 1 -> "🥇 1er";
            case 2 -> "🥈 2ème";
            case 3 -> "🥉 3ème";
            default -> rank + "ème";
        };
    }

    /**
     * Vérifie si l'étudiant a un classement
     */
    public boolean hasRank() {
        return rank != null && totalStudents != null;
    }

    /**
     * Vérifie si l'étudiant est dans le top 10%
     */
    public boolean isInTopPercent(int percent) {
        if (rank == null || totalStudents == null || totalStudents == 0) {
            return false;
        }
        int topCount = (int) Math.ceil(totalStudents * (percent / 100.0));
        return rank <= topCount;
    }

    // ===== Méthodes pour les notes =====

    /**
     * Obtient la moyenne générale formatée
     */
    public String getAverageFormatted() {
        if (averageScore == null) {
            return "N/A";
        }
        return String.format("%.2f", averageScore);
    }

    /**
     * Obtient la note la plus élevée
     */
    public Double getHighestGrade() {
        if (grades == null || grades.isEmpty()) {
            return null;
        }
        return grades.values().stream().max(Double::compareTo).orElse(null);
    }

    /**
     * Obtient la note la plus basse
     */
    public Double getLowestGrade() {
        if (grades == null || grades.isEmpty()) {
            return null;
        }
        return grades.values().stream().min(Double::compareTo).orElse(null);
    }

    /**
     * Obtient les sujets avec les notes
     */
    public Set<String> getSubjects() {
        return grades != null ? grades.keySet() : new HashSet<>();
    }
}