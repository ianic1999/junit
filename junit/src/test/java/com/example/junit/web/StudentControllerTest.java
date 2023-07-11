package com.example.junit.web;

import com.example.junit.domain.Discipline;
import com.example.junit.service.GradeService;
import com.example.junit.service.StudentService;
import com.example.junit.web.dto.GradeDto;
import com.example.junit.web.dto.GradeRequest;
import com.example.junit.web.dto.StudentDto;
import com.example.junit.web.dto.StudentRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;
    @MockBean
    private GradeService gradeService;

    @Test
    public void get_whenInvoked_expectedResponse() throws Exception {
        GradeDto gradeDto = new GradeDto(1L, 9.0, Discipline.ALGEBRA);
        StudentDto dto = new StudentDto(1L, "John", "White", "G11", List.of(gradeDto));
        when(studentService.get()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("White")))
                .andExpect(jsonPath("$[0].group", is("G11")))
                .andExpect(jsonPath("$[0].grades", hasSize(1)));
    }

    @Test
    public void getGradesForStudent_whenInvoked_expectedResponse() throws Exception {
        GradeDto gradeDto = new GradeDto(1L, 9.0, Discipline.ALGEBRA);
        when(gradeService.getForStudent(1L)).thenReturn(List.of(gradeDto));

        mockMvc.perform(get("/api/students/{id}/grades", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].value", is(9.0)))
                .andExpect(jsonPath("$[0].discipline", is("ALGEBRA")));
    }

    @Test
    public void getAverageGradePerDiscipline_whenInvoked_expectedResponse() throws Exception {
        Map<Discipline, Double> result = Map.of(Discipline.ALGEBRA, 9.0, Discipline.WEB_DEVELOPMENT, 9.3);
        when(gradeService.getAveragePerDiscipline(1L)).thenReturn(result);

        mockMvc.perform(get("/api/students/{id}/average", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ALGEBRA", is(9.0)))
                .andExpect(jsonPath("$.WEB_DEVELOPMENT", is(9.3)));
    }

    @Test
    public void add_whenInvoked_callsService() throws Exception {
        StudentRequest request = new StudentRequest("John", "White", "G11");
        when(studentService.add(request)).thenReturn(new StudentDto(1L, "John", "White", "G11", Collections.emptyList()));

        mockMvc.perform(post("/api/students")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                   {
                                       "firstName": "John",
                                       "lastName": "White",
                                       "group": "G11"
                                   }
                                """))
                .andExpect(status().isCreated());
        verify(studentService).add(request);
    }

    @Test
    public void addGrade_whenInvoked_callsService() throws Exception {
        GradeRequest request = new GradeRequest(null, 9.0, Discipline.ALGEBRA);
        when(gradeService.add(request)).thenReturn(new GradeDto(1L, 9.0, Discipline.ALGEBRA));
        ArgumentCaptor<GradeRequest> argumentCaptor = ArgumentCaptor.forClass(GradeRequest.class);

        mockMvc.perform(post("/api/students/{id}/grades", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                   {
                                       "value": 9.0,
                                       "discipline": "ALGEBRA"
                                   }
                                """))
               .andExpect(status().isCreated());
        verify(gradeService).add(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getStudentId()).isEqualTo(1);
    }

}
