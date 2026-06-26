package com.edueasy.common.enums;

public enum AdminPermission {
    // Permissions générales
    VIEW_TICKETS,
    MANAGE_TICKETS,
    ALL_ACCESS,
    MANAGE_SYSTEM,
    VIEW_REPORTS,
    EXPORT_DATA,
    IMPORT_DATA,

    // Gestion des utilisateurs
    MANAGE_USERS,
    MANAGE_STUDENTS,
    MANAGE_TEACHERS,
    MANAGE_ADMINS,
    MANAGE_PARENTS,
    MANAGE_STAFF,
    MANAGE_ROLES,
    MANAGE_PERMISSIONS,

    // Gestion des écoles
    MANAGE_SCHOOLS,
    MANAGE_CLASSES,
    MANAGE_DEPARTMENTS,
    MANAGE_GRADES,

    // Gestion des cours
    MANAGE_COURSES,
    MANAGE_CONTENT,
    MANAGE_CATEGORIES,
    MANAGE_TAGS,
    MANAGE_ENROLLMENTS,

    // Gestion des évaluations
    MANAGE_ASSESSMENTS,
    MANAGE_QUESTIONS,
    MANAGE_EXAMS,
   // MANAGE_GRADES,
    MANAGE_SUBMISSIONS,

    // Gestion des paiements
    MANAGE_PAYMENTS,
    MANAGE_INVOICES,
    MANAGE_SUBSCRIPTIONS,
    MANAGE_TRANSACTIONS,

    // Gestion des présences
    MANAGE_ATTENDANCE,
    VIEW_ATTENDANCE,

    // Gestion de la communication
    MANAGE_COMMUNICATION,
    SEND_NOTIFICATIONS,
    MANAGE_ANNOUNCEMENTS,
    COMMUNICATE_WITH_TEACHERS,

    // Accès en lecture
    VIEW_COURSES,
    VIEW_GRADES,
    VIEW_ATTENDANCE_RECORDS,

    // Actions étudiantes
    SUBMIT_ASSIGNMENTS,
    TAKE_EXAMS,
    VIEW_LEARNING_PROGRESS,

    // Autres
    MANAGE_TIMETABLES,
    MANAGE_RESOURCES,
    MANAGE_BACKUPS
}
