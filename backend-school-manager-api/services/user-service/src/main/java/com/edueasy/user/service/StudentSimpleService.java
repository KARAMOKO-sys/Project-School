package com.edueasy.user.service;

import com.edueasy.common.dto.ChangePasswordRequestDTO;
import com.edueasy.common.dto.StudentRequestDTO;
import com.edueasy.common.dto.StudentResponseDTO;
import com.edueasy.common.dto.StudentSimpleRegisterDTO;
import com.edueasy.common.dto.UserStatusUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentSimpleService {
    StudentResponseDTO registerStudent(StudentRequestDTO requestDTO);

    StudentResponseDTO registerStudentSimple(StudentSimpleRegisterDTO requestDTO);

    StudentResponseDTO getStudentByUuid(String studentUuid);

    StudentResponseDTO getStudentByEmail(String email);

    StudentResponseDTO updateStudent(String studentUuid, StudentRequestDTO requestDTO);

    StudentResponseDTO updateProfilStudentSimple(String studentSimpleUuid, StudentRequestDTO requestDTO);

    void deleteStudent(String studentId);

    void permanentDeleteStudent(String studentId);

    void updateStudentStatus(String studentId, UserStatusUpdateDTO statusUpdate);

    void changePassword(String studentId, ChangePasswordRequestDTO passwordRequest);

    Page<StudentResponseDTO> getAllStudents(Pageable pageable);

    Page<StudentResponseDTO> searchStudents(String keyword, Pageable pageable);

    void updateLastLogin(String studentId);

    boolean existsByEmail(String email);

    long getActiveStudentsCount();

    long getTodayRegistrationsCount();
}