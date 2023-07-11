package com.example.junit.web;

import com.example.junit.domain.Discipline;
import com.example.junit.service.GradeService;
import com.example.junit.service.StudentService;
import com.example.junit.web.dto.GradeDto;
import com.example.junit.web.dto.GradeRequest;
import com.example.junit.web.dto.StudentDto;
import com.example.junit.web.dto.StudentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final GradeService gradeService;

    @GetMapping
    public ResponseEntity<List<StudentDto>> get() {
        return ResponseEntity.ok(studentService.get());
    }

    @GetMapping("/{id}/grades")
    public ResponseEntity<List<GradeDto>> getGradesForStudent(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.getForStudent(id));
    }

    @GetMapping("/{id}/average")
    public ResponseEntity<Map<Discipline, Double>> getAverageGradePerDiscipline(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.getAveragePerDiscipline(id));
    }

    @GetMapping("/{id}/max")
    public ResponseEntity<Map<Discipline, Double>> getMaxGradePerDiscipline(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.getMaxPerDiscipline(id));
    }

    @GetMapping("/{id}/promotion")
    public ResponseEntity<Map<Discipline, Boolean>> isPromotedPerDiscipline(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.isPromotedPerDiscipline(id));
    }

    @PostMapping
    public ResponseEntity<StudentDto> add(@RequestBody StudentRequest request) {
        return new ResponseEntity<>(studentService.add(request), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/grades")
    public ResponseEntity<GradeDto> addGrade(@PathVariable Long id,
                                             @RequestBody GradeRequest request) {
        request.setStudentId(id);
        return new ResponseEntity<>(gradeService.add(request), HttpStatus.CREATED);
    }
}
