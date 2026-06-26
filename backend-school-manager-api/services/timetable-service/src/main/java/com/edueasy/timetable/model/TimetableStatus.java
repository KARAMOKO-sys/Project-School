package com.edueasy.timetable.model;

public enum TimetableStatus {
    POSTPONED,
    ONGOING,
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    RESCHEDULED;

    private TimetableStatus() {
    }
}
