package com.edueasy.attendance.model;

public enum AttendanceStatus {
    UNJUSTIFIED,
    PRESENT,
    ABSENT,
    LATE,
    EXCUSED,
    HOLIDAY,
    SICK_LEAVE;

    private AttendanceStatus() {
    }
}
