package com.fahmak.alena.course.controller;

import com.fahmak.alena.course.dto.CourseRequest;
import com.fahmak.alena.course.dto.CourseResponse;
import com.fahmak.alena.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<CourseResponse>> getRecommendedCourses() {
        return ResponseEntity.ok(courseService.getRecommendedCourses());
    }

    @GetMapping("/my-enrollments")
    public ResponseEntity<List<CourseResponse>> getMyEnrollments(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(courseService.getMyEnrollments(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CourseResponse> createCourse(
            @RequestBody CourseRequest request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(courseService.createCourse(request, email));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseRequest request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(courseService.updateCourse(id, request, email));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        courseService.deleteCourse(id, email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, String>> enrollInCourse(
            @PathVariable Long id,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized"));
        }
        
        courseService.enrollInCourse(id, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Successfully enrolled in course"));
    }
}
