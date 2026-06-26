package com.edueasy.user.service;

import com.edueasy.common.dto.ChangePasswordRequestDTO;
import com.edueasy.common.dto.TeacherAuthResponseDTO;
import com.edueasy.common.dto.TeacherRequestDTO;
import com.edueasy.common.dto.TeacherResponseDTO;
import com.edueasy.common.dto.TeacherSimpleRegisterDTO;
import com.edueasy.common.dto.TeacherSimpleRequestDTO;
import com.edueasy.common.dto.UserStatusUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface TeacherSimpleService {
    TeacherResponseDTO registerTeacher(TeacherRequestDTO requestDTO);

    TeacherAuthResponseDTO registerTeacherSimple(TeacherSimpleRegisterDTO requestDTO);

    TeacherResponseDTO getTeacherById(String teacherId);

    TeacherResponseDTO getTeacherByEmail(String email);

    TeacherResponseDTO updateTeacherSimple(String teacherUuid, TeacherSimpleRegisterDTO requestDTO);

    TeacherResponseDTO updateProfilTeacherSimple(String teacherUuid, TeacherSimpleRequestDTO requestDTO);

    @Transactional
    void deleteTeacher(String teacherUuid);

    void permanentDeleteTeacher(String teacherId);

    void updateTeacherStatus(String teacherId, UserStatusUpdateDTO statusUpdate);

    void changePassword(String teacherId, ChangePasswordRequestDTO passwordRequest);

    Page<TeacherResponseDTO> getAllTeachers(Pageable pageable);

    Page<TeacherResponseDTO> searchTeachers(String keyword, Pageable pageable);

    void updateLastLogin(String teacherId);

    boolean existsByEmail(String email);

    long getActiveTeachersCount();
}
