package com.edueasy.course.service;

public interface ContentExportService {
    byte[] exportCourseContentsToPdf(String courseUuid);

    byte[] exportContentAsFile(String contentUuid);

    String getContentUrl(String contentUuid);
}
