package com.fahmak.alena.course.service;

import com.fahmak.alena.course.dto.CourseRequest;
import com.fahmak.alena.course.dto.CourseResponse;
import com.fahmak.alena.course.entity.Course;
import com.fahmak.alena.course.entity.CourseEnrollment;
import com.fahmak.alena.course.repository.CourseEnrollmentRepository;
import com.fahmak.alena.course.repository.CourseRepository;
import com.fahmak.alena.notification.service.NotificationService;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock private CourseRepository courseRepository;
    @Mock private CourseEnrollmentRepository courseEnrollmentRepository;
    @Mock private UserService userService;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCourses_ShouldReturnList() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Java 101");
        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<CourseResponse> result = courseService.getAllCourses();

        assertEquals(1, result.size());
        assertEquals("Java 101", result.get(0).getTitle());
    }

    @Test
    void createCourse_ShouldSaveAndReturnCourse() {
        CourseRequest req = new CourseRequest();
        req.setTitle("New Course");
        
        User instructor = new User();
        instructor.setEmail("instructor@test.com");
        when(userService.findByEmail("instructor@test.com")).thenReturn(Optional.of(instructor));

        Course savedCourse = new Course();
        savedCourse.setId(10L);
        savedCourse.setTitle("New Course");
        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        CourseResponse result = courseService.createCourse(req, "instructor@test.com");

        assertEquals(10L, result.getId());
        assertEquals("New Course", result.getTitle());
    }

    @Test
    void enrollInCourse_Success() {
        User user = new User();
        user.setId(5L);
        when(userService.findByEmail("student@test.com")).thenReturn(Optional.of(user));

        Course course = new Course();
        course.setId(1L);
        course.setTitle("Java 101");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        when(courseEnrollmentRepository.existsByStudentAndCourse(user, course)).thenReturn(false);

        courseService.enrollInCourse(1L, "student@test.com");

        verify(courseEnrollmentRepository).save(any(CourseEnrollment.class));
        verify(notificationService).createNotification(eq(5L), eq("Course Enrolled"), anyString(), any());
    }

    @Test
    void enrollInCourse_AlreadyEnrolled_ThrowsException() {
        User user = new User();
        Course course = new Course();
        
        when(userService.findByEmail("student@test.com")).thenReturn(Optional.of(user));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseEnrollmentRepository.existsByStudentAndCourse(user, course)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> courseService.enrollInCourse(1L, "student@test.com"));
        verify(courseEnrollmentRepository, never()).save(any());
    }
}
