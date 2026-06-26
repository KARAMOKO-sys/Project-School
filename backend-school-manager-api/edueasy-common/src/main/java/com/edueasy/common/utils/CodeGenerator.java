package com.edueasy.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CodeGenerator {

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyyMMdd");

    public CodeGenerator() {}

    public static String generateTeacherNumber() {
        String date = LocalDateTime.now().format(DATE_FORMATTER);
        String random = UUID.randomUUID()
            .toString()
            .substring(0, 4)
            .toUpperCase();
        return String.format("TCH-%s-%s", date, random);
    }

    public static String generateStudentNumber() {
        String date = LocalDateTime.now().format(DATE_FORMATTER);
        String random = UUID.randomUUID()
            .toString()
            .substring(0, 4)
            .toUpperCase();
        return String.format("STU-%s-%s", date, random);
    }

    public static String generateTransactionCode() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "yyyyMMddHHmmss"
        );
        String dateTime = LocalDateTime.now().format(formatter);
        String random = UUID.randomUUID()
            .toString()
            .substring(0, 6)
            .toUpperCase();
        return String.format("TXN-%s-%s", dateTime, random);
    }

    public static String generateVerificationCode() {
        return String.format(
            "%06d",
            (int) (Math.random() * (double) 1000000.0F)
        );
    }

    public static String generateSimpleUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String generateCode(String prefix) {
        String date = LocalDateTime.now().format(DATE_FORMATTER);
        String random = UUID.randomUUID()
            .toString()
            .substring(0, 4)
            .toUpperCase();
        return String.format("%s-%s-%s", prefix.toUpperCase(), date, random);
    }
}
