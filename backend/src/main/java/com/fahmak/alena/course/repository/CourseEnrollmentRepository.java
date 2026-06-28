package com.fahmak.alena.course.repository;

import com.fahmak.alena.course.entity.CourseEnrollment;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"student", "course"})
    List<CourseEnrollment> findByStudent(User student);
    
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"student", "course"})
    List<CourseEnrollment> findByCourse(Course course);
    
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"student", "course"})
    List<CourseEnrollment> findByCourseIn(List<Course> courses);
    
    Optional<CourseEnrollment> findByStudentAndCourse(User student, Course course);
    boolean existsByStudentAndCourse(User student, Course course);
}
