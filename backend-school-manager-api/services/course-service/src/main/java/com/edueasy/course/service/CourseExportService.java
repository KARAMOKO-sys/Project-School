package com.edueasy.course.service;

public interface CourseExportService {
    byte[] exportCoursesToCsv(String teacherUuid);

    byte[] exportCoursesToPdf(String teacherUuid);

    byte[] exportTeacherCoursesToCsv(String teacherUuid);
}
