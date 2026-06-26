package com.edueasy.course.client;

import com.edueasy.common.dto.StudentResponseDTO;
import com.edueasy.common.dto.TeacherResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "${user.service.url:http://localhost:8081}"
)
public interface UserServiceClient {
    @GetMapping({"/api/teachers-simple/{teacherUuid}"})
    TeacherResponseDTO getTeacherByUuid(@PathVariable("teacherUuid") String teacherUuid);

    @GetMapping({"/api/teachers-simple/exists/{teacherUuid}"})
    boolean checkTeacherExists(@PathVariable("teacherUuid") String teacherUuid);

    @GetMapping({"/api/students-simple/{studentUuid}"})
    StudentResponseDTO getStudentByUuid(@PathVariable("studentUuid") String studentUuid);
}