package com.example.junit.service;

import com.example.junit.domain.Student;
import com.example.junit.repository.StudentRepository;
import com.example.junit.service.mapper.Mapper;
import com.example.junit.web.dto.StudentDto;
import com.example.junit.web.dto.StudentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final Mapper<Student, StudentDto> studentDtoMapper;

    @Transactional(readOnly = true)
    public List<StudentDto> get() {
        return studentDtoMapper.mapList(studentRepository.findAll());
    }

    @Transactional
    public StudentDto add(StudentRequest request) {
        Student student = new Student(request.getFirstName(), request.getLastName(), request.getGroup());
        return studentDtoMapper.map(studentRepository.save(student));
    }
}
