package com.example.junit.service.mapper;

import com.example.junit.domain.Grade;
import com.example.junit.domain.Student;
import com.example.junit.web.dto.GradeDto;
import com.example.junit.web.dto.StudentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class StudentDtoMapper implements Mapper<Student, StudentDto> {

    private final Mapper<Grade, GradeDto> gradeDtoMapper;

    @Override
    public StudentDto map(Student entity) {
        return new StudentDto(entity.getId(),
                              entity.getFirstName(),
                              entity.getLastName(),
                              entity.getGroup(),
                              gradeDtoMapper.mapList(entity.getGrades()));
    }
}
