package com.edueasy.common.enums;

import java.util.Set;

public enum UserRole {
    SUPER_ADMIN(Set.of(
            AdminPermission.ALL_ACCESS,
            AdminPermission.MANAGE_USERS,
            AdminPermission.MANAGE_SCHOOLS,
            AdminPermission.MANAGE_COURSES,
            AdminPermission.MANAGE_PAYMENTS,
            AdminPermission.VIEW_REPORTS,
            AdminPermission.MANAGE_SYSTEM
    )),

    ADMIN(Set.of(
            AdminPermission.MANAGE_USERS,
            AdminPermission.MANAGE_SCHOOLS,
            AdminPermission.MANAGE_COURSES,
            AdminPermission.MANAGE_PAYMENTS,
            AdminPermission.VIEW_REPORTS
    )),

    ADMINISTRATOR(Set.of(
            AdminPermission.MANAGE_USERS,
            AdminPermission.MANAGE_SCHOOLS,
            AdminPermission.MANAGE_COURSES,
            AdminPermission.MANAGE_PAYMENTS,
            AdminPermission.VIEW_REPORTS
    )),

    DIRECTOR(Set.of(
            AdminPermission.MANAGE_USERS,
            AdminPermission.MANAGE_COURSES,
            AdminPermission.VIEW_REPORTS,
            AdminPermission.MANAGE_STAFF
    )),

    TEACHER(Set.of(
            AdminPermission.MANAGE_COURSES,
            AdminPermission.MANAGE_ASSESSMENTS,
            AdminPermission.MANAGE_GRADES,
            AdminPermission.VIEW_REPORTS,
            AdminPermission.MANAGE_ATTENDANCE
    )),

    TEACHER_SIMPLE(Set.of(
            AdminPermission.VIEW_COURSES,
            AdminPermission.MANAGE_ATTENDANCE,
            AdminPermission.VIEW_REPORTS,
            AdminPermission.SUBMIT_ASSIGNMENTS
    )),

    STUDENT(Set.of(
            AdminPermission.VIEW_COURSES,
            AdminPermission.SUBMIT_ASSIGNMENTS,
            AdminPermission.VIEW_GRADES,
            AdminPermission.VIEW_REPORTS
    )),

    // AJOUT : STUDENT_SIMPLE
    STUDENT_SIMPLE(Set.of(
            AdminPermission.VIEW_COURSES,
            AdminPermission.VIEW_GRADES,
            AdminPermission.VIEW_REPORTS
    )),

    PARENT(Set.of(
            AdminPermission.VIEW_REPORTS,
            AdminPermission.VIEW_COURSES,
            AdminPermission.VIEW_GRADES,
            AdminPermission.COMMUNICATE_WITH_TEACHERS
    )),

    GUARDIAN(Set.of(
            AdminPermission.VIEW_REPORTS,
            AdminPermission.VIEW_COURSES,
            AdminPermission.VIEW_GRADES,
            AdminPermission.COMMUNICATE_WITH_TEACHERS
    )),

    SUPPORT_STAFF(Set.of(
            AdminPermission.MANAGE_USERS,
            AdminPermission.VIEW_REPORTS
    )),

    SUPPORT_AGENT(Set.of(
            AdminPermission.MANAGE_USERS,
            AdminPermission.VIEW_REPORTS,
            AdminPermission.MANAGE_TICKETS,
            AdminPermission.VIEW_TICKETS
    ));

    private final Set<AdminPermission> permissions;

    UserRole(Set<AdminPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<AdminPermission> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(AdminPermission permission) {
        return permissions.contains(permission);
    }

    public boolean isAdminRole() {
        return this == SUPER_ADMIN || this == ADMIN || this == ADMINISTRATOR || this == DIRECTOR;
    }

    public boolean isTeacherRole() {
        return this == TEACHER || this == TEACHER_SIMPLE;
    }

    public boolean isStudentRole() {
        return this == STUDENT || this == STUDENT_SIMPLE;
    }

    public boolean isParentOrGuardian() {
        return this == PARENT || this == GUARDIAN;
    }

    public boolean isManagementRole() {
        return isAdminRole() || this == SUPPORT_STAFF || this == SUPPORT_AGENT;
    }

    public boolean isSupportRole() {
        return this == SUPPORT_STAFF || this == SUPPORT_AGENT;
    }

    public boolean isSuperAdmin() {
        return this == SUPER_ADMIN;
    }

    public boolean isSimpleRole() {
        return this == STUDENT_SIMPLE || this == TEACHER_SIMPLE;
    }

    public String getDisplayName() {
        return switch (this) {
            case SUPER_ADMIN -> "Super Administrateur";
            case ADMIN, ADMINISTRATOR -> "Administrateur";
            case DIRECTOR -> "Directeur";
            case TEACHER -> "Enseignant";
            case TEACHER_SIMPLE -> "Enseignant Simplifié";
            case STUDENT -> "Étudiant";
            case STUDENT_SIMPLE -> "Étudiant Simplifié";
            case PARENT -> "Parent";
            case GUARDIAN -> "Tuteur";
            case SUPPORT_STAFF -> "Personnel de soutien";
            case SUPPORT_AGENT -> "Agent de support";
        };
    }

    public String getRoleCode() {
        return switch (this) {
            case SUPER_ADMIN -> "SA";
            case ADMIN, ADMINISTRATOR -> "ADM";
            case DIRECTOR -> "DIR";
            case TEACHER -> "TCH";
            case TEACHER_SIMPLE -> "TCHS";
            case STUDENT -> "STU";
            case STUDENT_SIMPLE -> "STUS";
            case PARENT -> "PAR";
            case GUARDIAN -> "GUA";
            case SUPPORT_STAFF -> "SUP";
            case SUPPORT_AGENT -> "SAG";
        };
    }

    public boolean canAccessAdminPanel() {
        return this == SUPER_ADMIN || this == ADMIN || this == ADMINISTRATOR || this == DIRECTOR;
    }

    public boolean canManageUsers() {
        return this == SUPER_ADMIN || this == ADMIN || this == ADMINISTRATOR ||
                this == SUPPORT_STAFF || this == SUPPORT_AGENT;
    }

    public boolean canViewReports() {
        return this == SUPER_ADMIN || this == ADMIN || this == ADMINISTRATOR ||
                this == DIRECTOR || this == TEACHER || this == TEACHER_SIMPLE ||
                this == STUDENT || this == STUDENT_SIMPLE ||
                this == PARENT || this == GUARDIAN || this == SUPPORT_STAFF || this == SUPPORT_AGENT;
    }

    public boolean canManageCourses() {
        return this == TEACHER || this == TEACHER_SIMPLE || this == ADMIN ||
                this == ADMINISTRATOR || this == SUPER_ADMIN || this == DIRECTOR;
    }

    public boolean canGradeStudents() {
        return this == TEACHER || this == TEACHER_SIMPLE || this == ADMIN || this == ADMINISTRATOR;
    }

    public boolean isEducationalRole() {
        return this == TEACHER || this == TEACHER_SIMPLE || this == STUDENT || this == STUDENT_SIMPLE;
    }

    public boolean isParentalRole() {
        return this == PARENT || this == GUARDIAN;
    }

    public static UserRole fromString(String value) {
        try {
            return UserRole.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}