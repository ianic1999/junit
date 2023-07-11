package com.example.junit.service;

import com.example.junit.domain.Discipline;
import com.example.junit.domain.Grade;
import com.example.junit.domain.Student;
import com.example.junit.repository.StudentRepository;
import com.example.junit.service.mapper.Mapper;
import com.example.junit.web.dto.GradeDto;
import com.example.junit.web.dto.GradeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    private static final long STUDENT_ID = 1L;

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private Mapper<Grade, GradeDto> gradeDtoMapper;

    private GradeService gradeService;

    private Student student;

    @BeforeEach
    public void setup() {
        gradeService = new GradeService(studentRepository, gradeDtoMapper);

        student = new Student("John", "White", "G11");
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
    }

    @Test
    public void add_whenInvoked_addsGradeToStudent() {
        GradeRequest request = new GradeRequest(STUDENT_ID, 9.5, Discipline.GEOMETRY);
        Grade expectedGrade = new Grade(9.5, Discipline.GEOMETRY);

        gradeService.add(request);

        assertThat(student.getGrades())
                .hasSize(1)
                .containsOnly(expectedGrade);
    }

    @Test
    public void getForStudent_whenStudentNotFound_exceptionThrown() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.getForStudent(STUDENT_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Student not found");
    }

    @Test
    public void getForStudent_whenInvoked_expectedResult() {
        Grade expectedGrade = new Grade(9.5, Discipline.GEOMETRY);
        student.addGrade(expectedGrade);
        GradeDto dto = new GradeDto(1L, 9.5, Discipline.GEOMETRY);
        when(gradeDtoMapper.mapList(student.getGrades())).thenReturn(List.of(dto));

        assertThat(gradeService.getForStudent(STUDENT_ID))
                .hasSize(1)
                .containsOnly(dto);
    }

    @Test
    public void getAveragePerDiscipline_whenInvoked_expectedResult() {
        student.addGrade(new Grade(9.0, Discipline.GEOMETRY));
        student.addGrade(new Grade(10.0, Discipline.GEOMETRY));
        student.addGrade(new Grade(9.5, Discipline.ALGEBRA));
        student.addGrade(new Grade(8.0, Discipline.ARTIFICIAL_INTELLIGENCE));
        student.addGrade(new Grade(10.0, Discipline.ARTIFICIAL_INTELLIGENCE));

        Map<Discipline, Double> result = gradeService.getAveragePerDiscipline(STUDENT_ID);

        assertThat(result).hasSize(3)
                .containsOnly(Map.entry(Discipline.GEOMETRY, 9.5),
                              Map.entry(Discipline.ALGEBRA, 9.5),
                              Map.entry(Discipline.ARTIFICIAL_INTELLIGENCE, 9.0));
    }

    @Test
    public void getMaxPerDiscipline_whenInvoked_expectedResult() {
        student.addGrade(new Grade(9.0, Discipline.GEOMETRY));
        student.addGrade(new Grade(10.0, Discipline.GEOMETRY));
        student.addGrade(new Grade(9.5, Discipline.ALGEBRA));
        student.addGrade(new Grade(8.0, Discipline.ARTIFICIAL_INTELLIGENCE));
        student.addGrade(new Grade(10.0, Discipline.ARTIFICIAL_INTELLIGENCE));

        Map<Discipline, Double> result = gradeService.getMaxPerDiscipline(STUDENT_ID);

        assertThat(result).hasSize(3)
                          .containsOnly(Map.entry(Discipline.GEOMETRY, 10.0),
                                        Map.entry(Discipline.ALGEBRA, 9.5),
                                        Map.entry(Discipline.ARTIFICIAL_INTELLIGENCE, 10.0));
    }

    @Test
    public void isPromotedPerDiscipline_whenInvoked_expectedResult() {
        student.addGrade(new Grade(9.0, Discipline.GEOMETRY));
        student.addGrade(new Grade(10.0, Discipline.GEOMETRY));
        student.addGrade(new Grade(9.5, Discipline.ALGEBRA));
        student.addGrade(new Grade(2.0, Discipline.ARTIFICIAL_INTELLIGENCE));
        student.addGrade(new Grade(5.0, Discipline.ARTIFICIAL_INTELLIGENCE));

        Map<Discipline, Boolean> result = gradeService.isPromotedPerDiscipline(STUDENT_ID);

        assertThat(result).hasSize(5);
        assertThat(result.get(Discipline.GEOMETRY)).isTrue();
        assertThat(result.get(Discipline.ALGEBRA)).isTrue();
        assertThat(result.get(Discipline.ARTIFICIAL_INTELLIGENCE)).isFalse();
        assertThat(result.get(Discipline.PROGRAMMING_FUNDAMENTALS)).isFalse();
        assertThat(result.get(Discipline.WEB_DEVELOPMENT)).isFalse();
    }
}
