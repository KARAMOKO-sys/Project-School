package com.edueasy.user.controller;

import com.edueasy.common.dto.TeacherDTO;
import com.edueasy.user.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/teachers"})
@Tag(
        name = "Teacher Management",
        description = "APIs pour la gestion des enseignants"
)
public class TeacherController {
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    @Operation(
            summary = "Créer un nouvel enseignant"
    )
    public ResponseEntity<TeacherDTO> createTeacher(@RequestBody @Valid TeacherDTO teacherDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.teacherService.createTeacher(teacherDTO));
    }

    @GetMapping({"/{id}"})
    @Operation(
            summary = "Récupérer un enseignant par son ID"
    )
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable String id) {
        return ResponseEntity.ok(this.teacherService.getTeacherById(id));
    }

    @GetMapping({"/number/{teacherNumber}"})
    @Operation(
            summary = "Récupérer un enseignant par son matricule"
    )
    public ResponseEntity<TeacherDTO> getTeacherByNumber(@PathVariable String teacherNumber) {
        return (ResponseEntity)this.teacherService.getTeacherByNumber(teacherNumber).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(
            summary = "Récupérer tous les enseignants"
    )
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        return ResponseEntity.ok(this.teacherService.getAllTeachers());
    }

    @PutMapping({"/{id}"})
    @Operation(
            summary = "Mettre à jour un enseignant"
    )
    public ResponseEntity<TeacherDTO> updateTeacher(@PathVariable String id, @RequestBody @Valid TeacherDTO teacherDTO) {
        return ResponseEntity.ok(this.teacherService.updateTeacher(id, teacherDTO));
    }

    @DeleteMapping({"/{id}"})
    @Operation(
            summary = "Supprimer un enseignant"
    )
    public ResponseEntity<Void> deleteTeacher(@PathVariable String id) {
        this.teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}
