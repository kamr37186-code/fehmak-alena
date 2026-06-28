package com.fahmak.alena.instructor.service;

import com.fahmak.alena.assessment.entity.IntegrityVerdict;
import com.fahmak.alena.assessment.entity.Quiz;
import com.fahmak.alena.assessment.entity.QuizSession;
import com.fahmak.alena.assessment.repository.QuizSessionRepository;
import com.fahmak.alena.course.entity.Course;
import com.fahmak.alena.course.entity.CourseEnrollment;
import com.fahmak.alena.course.service.CourseService;
import com.fahmak.alena.instructor.dto.InstructorDashboardResponse;
import com.fahmak.alena.assessment.dto.IntegrityReportResponse;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class InstructorServiceTest {

    @Mock private UserService userService;
    @Mock private CourseService courseService;
    @Mock private QuizSessionRepository quizSessionRepository;

    @InjectMocks
    private InstructorService instructorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDashboardData_ShouldReturnStats() {
        User instructor = new User();
        instructor.setId(1L);
        instructor.setFirstName("Inst");
        instructor.setLastName("Ruct");
        when(userService.findByEmail("inst@test.com")).thenReturn(Optional.of(instructor));

        Course course = new Course();
        course.setTitle("Math 101");
        when(courseService.getInstructorCourses(instructor)).thenReturn(List.of(course));

        User student = new User();
        student.setId(10L);
        student.setFirstName("Stu");
        student.setLastName("Dent");
        student.setEmail("stu@test.com");

        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setProgressPercentage(50);

        when(courseService.getEnrollmentsForCourses(any())).thenReturn(List.of(enrollment));

        InstructorDashboardResponse response = instructorService.getDashboardData("inst@test.com");

        assertNotNull(response);
        assertEquals(1, response.getTotalStudents());
        assertEquals(1, response.getStudentProgress().size());
        assertEquals(50, response.getStudentProgress().get(0).getProgressPercentage());
    }

    @Test
    void getFlaggedSessions_ShouldReturnFlaggedOnly() {
        User instructor = new User();
        instructor.setEmail("inst@test.com");
        when(userService.findByEmail("inst@test.com")).thenReturn(Optional.of(instructor));

        QuizSession session1 = new QuizSession();
        session1.setId(1L);
        session1.setIntegrityVerdict(IntegrityVerdict.FLAGGED);
        session1.setUser(new User());
        Course course = new Course();
        course.setId(99L);
        course.setInstructor(instructor);
        when(courseService.getInstructorCourses(instructor)).thenReturn(List.of(course));
        Quiz quiz1 = new Quiz();
        quiz1.setCourse(course);
        session1.setQuiz(quiz1);
        
        QuizSession session2 = new QuizSession();
        session2.setId(2L);
        session2.setIntegrityVerdict(IntegrityVerdict.CLEAN);
        session2.setUser(new User());
        Quiz quiz2 = new Quiz();
        quiz2.setCourse(course);
        session2.setQuiz(quiz2);

        when(quizSessionRepository.findAll()).thenReturn(List.of(session1, session2));

        List<IntegrityReportResponse> response = instructorService.getFlaggedSessions("inst@test.com");
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getSessionId());
        assertEquals(IntegrityVerdict.FLAGGED, response.get(0).getVerdict());
    }
}
