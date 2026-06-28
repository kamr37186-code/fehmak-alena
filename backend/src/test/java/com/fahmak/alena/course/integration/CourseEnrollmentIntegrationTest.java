package com.fahmak.alena.course.integration;

import com.fahmak.alena.course.entity.Course;
import com.fahmak.alena.course.entity.CourseEnrollment;
import com.fahmak.alena.course.repository.CourseEnrollmentRepository;
import com.fahmak.alena.course.repository.CourseRepository;
import com.fahmak.alena.user.entity.Role;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourseEnrollmentIntegrationTest {

    @Autowired
    private CourseEnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndRetrieveEnrollment() {
        // Create instructor
        User instructor = new User();
        instructor.setUsername("prof");
        instructor.setEmail("prof@test.com");
        instructor.setFirstName("Prof");
        instructor.setLastName("Test");
        instructor.setRole(Role.INSTRUCTOR);
        instructor = userRepository.save(instructor);

        // Create student
        User student = new User();
        student.setUsername("student");
        student.setEmail("student@test.com");
        student.setFirstName("Student");
        student.setLastName("Test");
        student.setRole(Role.STUDENT);
        student = userRepository.save(student);

        // Create Course
        Course course = new Course();
        course.setTitle("Integration Testing 101");
        course.setDescription("Learn to test");
        course.setInstructor(instructor);
        course.setPrice(100.0);
        course.setCreationDate(LocalDateTime.now());
        course.setLastUpdatedDate(LocalDateTime.now());
        course = courseRepository.save(course);

        // Create Enrollment
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setProgressPercentage(0);
        enrollment = enrollmentRepository.save(enrollment);

        assertThat(enrollment.getId()).isNotNull();

        // Retrieve Enrollment
        Optional<CourseEnrollment> found = enrollmentRepository.findById(enrollment.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCourse().getTitle()).isEqualTo("Integration Testing 101");
        assertThat(found.get().getStudent().getEmail()).isEqualTo("student@test.com");
    }
}
