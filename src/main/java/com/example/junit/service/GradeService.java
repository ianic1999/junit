package com.example.junit.service;

import com.example.junit.domain.Discipline;
import com.example.junit.domain.Grade;
import com.example.junit.domain.Student;
import com.example.junit.repository.StudentRepository;
import com.example.junit.service.mapper.Mapper;
import com.example.junit.web.dto.GradeDto;
import com.example.junit.web.dto.GradeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {

    private static final double PROMOTE_THRESHOLD = 4.5;

    private final StudentRepository studentRepository;
    private final Mapper<Grade, GradeDto> gradeDtoMapper;

    @Transactional
    public GradeDto add(GradeRequest request) {
        final Student student = studentRepository.findById(request.getStudentId()).orElseThrow(() -> new RuntimeException("Student not found"));
        Grade grade = new Grade(request.getValue(), request.getDiscipline());
        student.addGrade(grade);
        studentRepository.flush();
        return gradeDtoMapper.map(grade);
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getForStudent(Long studentId) {
        final Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
        return gradeDtoMapper.mapList(student.getGrades());
    }

    @Transactional(readOnly = true)
    public Map<Discipline, Double> getAveragePerDiscipline(Long studentId) {
        final Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
        return student.getGrades().stream()
                .collect(Collectors.groupingBy(Grade::getDiscipline, Collectors.averagingDouble(Grade::getValue)));
    }

    @Transactional(readOnly = true)
    public Map<Discipline, Double> getMaxPerDiscipline(Long studentId) {
        final Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
        return student.getGrades().stream()
                      .collect(Collectors.toUnmodifiableMap(Grade::getDiscipline, Grade::getValue, Double::max));
    }

    @Transactional(readOnly = true)
    public Map<Discipline, Boolean> isPromotedPerDiscipline(Long studentId) {
        final Map<Discipline, Boolean> map = getAveragePerDiscipline(studentId).entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue() > PROMOTE_THRESHOLD))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Discipline, Boolean> result = Arrays.stream(Discipline.values()).collect(Collectors.toMap(Function.identity(), d -> false));
        result.putAll(map);
        return result;
    }
}
