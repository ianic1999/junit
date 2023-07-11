package com.example.junit.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class StudentDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String group;
    private List<GradeDto> grades;
}
