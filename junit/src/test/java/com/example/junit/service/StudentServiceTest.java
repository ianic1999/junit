package com.example.junit.service;

import com.example.junit.domain.Student;
import com.example.junit.repository.StudentRepository;
import com.example.junit.service.mapper.Mapper;
import com.example.junit.web.dto.StudentDto;
import com.example.junit.web.dto.StudentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private Mapper<Student, StudentDto> studentDtoMapper;

    private StudentService studentService;

    @BeforeEach
    public void setup() {
        studentService = new StudentService(studentRepository, studentDtoMapper);
    }

    @Test
    public void add_whenInvoked_savesWithCorrectData() {
        StudentRequest request = new StudentRequest("John", "White", "G11");
        Student student = new Student("John", "White", "G11");
        StudentDto dto = new StudentDto(1L, "John", "White", "G11", Collections.emptyList());
        ArgumentCaptor<Student> argumentCaptor = ArgumentCaptor.forClass(Student.class);
        when(studentRepository.save(argumentCaptor.capture())).thenReturn(student);
        when(studentDtoMapper.map(student)).thenReturn(dto);

        studentService.add(request);

        Student savedStudent = argumentCaptor.getValue();
        assertThat(savedStudent.getFirstName()).isEqualTo("John");
        assertThat(savedStudent.getLastName()).isEqualTo("White");
        assertThat(savedStudent.getGroup()).isEqualTo("G11");
        assertThat(savedStudent.getGrades()).isEmpty();
    }

}
