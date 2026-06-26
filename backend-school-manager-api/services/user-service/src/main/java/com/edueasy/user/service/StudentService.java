package com.edueasy.user.service;

import com.edueasy.common.dto.TeacherDTO;
import java.util.List;
import java.util.Optional;

public class StudentService {
    public StudentService() {
    }

    public interface TeacherService {
        TeacherDTO createTeacher(TeacherDTO teacherDTO);

        TeacherDTO getTeacherById(String id);

        Optional<TeacherDTO> getTeacherByNumber(String teacherNumber);

        List<TeacherDTO> getAllTeachers();

        TeacherDTO updateTeacher(String id, TeacherDTO teacherDTO);

        void deleteTeacher(String id);
    }
}