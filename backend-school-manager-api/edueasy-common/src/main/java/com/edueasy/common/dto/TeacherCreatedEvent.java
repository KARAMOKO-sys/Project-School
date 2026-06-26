package com.edueasy.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCreatedEvent implements Serializable {
    private String eventId;
    private String teacherId;
    private String teacherNumber;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdAt;
    private String eventType;
}
