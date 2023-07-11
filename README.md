# Testing Spring Boot Project with JUnit 5, Mockito, and MockMvc

## Introduction
This project provides a step-by-step guide on testing a Spring Boot project using different tools

## Dependencies
Ensure that the following dependencies are included in your project's build file (pom.xml or build.gradle):

- JUnit 5
- Mockito
- Spring Boot Test
- Spring Boot Starter Web
- AssertJ

## JUnit
JUnit is a widely-used testing framework for Java applications. It provides a simple and convenient way to write and execute automated tests for Java code. JUnit is specifically designed to support unit testing, which involves testing individual units (e.g., methods, classes, or components) in isolation to ensure their correctness. Some key features and benefits of JUnit include: **Annotations**, **Assertions**, **Test Runners**, **Test Suites**, **Parameterized Tests**, **Test Fixtures** etc.

## Mockito
Mockito is a popular Java framework used for creating and working with mock objects in unit testing. It provides a simple and flexible API for creating mock objects, defining their behavior, and verifying interactions with them. Mockito is commonly used in conjunction with testing frameworks like JUnit to write comprehensive and effective unit tests. Key features and benefits of Mockito include: **Mock Objects**, **Behavior Verification**, **Stubbing**, **Argument Matching**, **Spy Objects**, **Annotations**, etc

## Project
The project exposes an API for managing students and their grades. Domain contains the following classes:
1. **Student**
   
```java
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(name = "group_number")
    private String group;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @Setter(AccessLevel.NONE)
    private List<Grade> grades = new ArrayList<>();
}
   ```

2. **Grade**

```java
  @Entity
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @Getter
  @Setter
  public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private Double value;

    @Enumerated(EnumType.STRING)
    private Discipline discipline;

    @ManyToOne
    private Student student;
}
```

## Writing Test Cases
A test case includes 3 steps (AAA): 
  1. **A**rrange (prepare test data)
  2. **A**ct (call method under test)
  3. **A**ssert (check expected behaviour)

### 1. Test a service
Steps:
1. Mock all the dependencies

   ```java
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private Mapper<Grade, GradeDto> gradeDtoMapper;
   ```
2. Setup all the needed data:

   ```java
    @BeforeEach
    public void setup() {
        gradeService = new GradeService(studentRepository, gradeDtoMapper);
    }
   ```

3. Write test case using AAA steps:

   ```java
    @Test
    public void isPromotedPerDiscipline_whenInvoked_expectedResult() {
        // Arrange 
        Student student = new Student("John", "White", "G11");
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student)); 
        student.addGrade(new Grade(9.0, Discipline.GEOMETRY));
        student.addGrade(new Grade(10.0, Discipline.GEOMETRY));
        student.addGrade(new Grade(9.5, Discipline.ALGEBRA));
        student.addGrade(new Grade(2.0, Discipline.ARTIFICIAL_INTELLIGENCE));
        student.addGrade(new Grade(5.0, Discipline.ARTIFICIAL_INTELLIGENCE));

        // Act
        Map<Discipline, Boolean> result = gradeService.isPromotedPerDiscipline(STUDENT_ID);

        // Assert
        assertThat(result).hasSize(5);
        assertThat(result.get(Discipline.GEOMETRY)).isTrue();
        assertThat(result.get(Discipline.ALGEBRA)).isTrue();
        assertThat(result.get(Discipline.ARTIFICIAL_INTELLIGENCE)).isFalse();
        assertThat(result.get(Discipline.PROGRAMMING_FUNDAMENTALS)).isFalse();
        assertThat(result.get(Discipline.WEB_DEVELOPMENT)).isFalse();
    }
   ```
### Note: 
For assertions, we use **AssertJ** library, which provides a fluent and expressive API for performing assertions in unit tests. It aims to enhance the readability and maintainability of test code by providing a wide range of assertion methods with a clear and intuitive syntax. 

### 2. Test a Web Layer class
For Web Layer, we use another approach for testing, because we need to load the Spring Application Context and test the corresponding endpoints using HTTP requests. For this, we will use **MockMvc**.

## MockMvc
MockMvc is a class provided by the Spring Framework that allows you to test your Spring MVC controllers in isolation, without deploying your application to a server. It provides a simulated environment for making HTTP requests and receiving responses, enabling you to perform integration-like testing for your web layer.

Steps:
1. Add necessary annotations to ensure that the context is loaded and configuration is performed:

   ```java
    @SpringBootTest
    @AutoConfigureMockMvc
   ```

2. Inject MockMvc bean and mock the beans on which the controller under test depends on.

   ```java
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;
    @MockBean
    private GradeService gradeService;
   ```
3. Write test case, using AAA steps. Here, *Act* and *Assert* steps are combined, as we perform them using **MockMvc**.

   ```java
    @Test
    public void get_whenInvoked_expectedResponse() throws Exception {
        // Arrange
        GradeDto gradeDto = new GradeDto(1L, 9.0, Discipline.ALGEBRA);
        StudentDto dto = new StudentDto(1L, "John", "White", "G11", List.of(gradeDto));
        when(studentService.get()).thenReturn(List.of(dto));

        // Act and Assert
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk()) // HTTP status
                .andExpect(jsonPath("$", hasSize(1))) // response body
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("White")))
                .andExpect(jsonPath("$[0].group", is("G11")))
                .andExpect(jsonPath("$[0].grades", hasSize(1)));
    }
   ```

### Note:
As we make HTTP requests, we get responses in JSON format, and we test the results using JSON paths.

## Conclusion
This markup documentation provided a step-by-step guide on testing a Spring Boot project using JUnit 5, Mockito, and MockMvc for the web layer. By following these guidelines, you can effectively test your Spring Boot application and ensure its correctness.
