package com.edueasy.assessment.model;

public enum SubmissionStatus {
    PENDING,
    UNDER_REVIEW,
    DRAFT,
    SUBMITTED,
    ACCEPTED,
    GRADED,
    REJECTED,
    LATE,
    MISSING,
    EXCUSED;

    private SubmissionStatus() {
    }
}