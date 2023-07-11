package com.example.junit.web.dto;

import com.example.junit.domain.Discipline;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GradeRequest {
    private Long studentId;
    private Double value;
    private Discipline discipline;
}
