package com.example.junit.web.dto;

import com.example.junit.domain.Discipline;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GradeDto {
    private Long id;
    private Double value;
    private Discipline discipline;
}
