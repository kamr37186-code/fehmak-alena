package com.fahmak.alena.course.service;

import com.fahmak.alena.course.dto.CourseRequest;
import com.fahmak.alena.course.dto.CourseResponse;
import com.fahmak.alena.course.entity.Course;
import com.fahmak.alena.course.entity.CourseEnrollment;
import com.fahmak.alena.course.repository.CourseEnrollmentRepository;
import com.fahmak.alena.course.repository.CourseRepository;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.service.UserService;
import com.fahmak.alena.notification.entity.NotificationType;
import com.fahmak.alena.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getRecommendedCourses() {
        return courseRepository.findTop3ByOrderByCreationDateDesc().stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return mapToCourseResponse(course);
    }

    @Transactional
    public CourseResponse createCourse(CourseRequest request, String instructorEmail) {
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setDifficultyLevel(request.getDifficultyLevel());
        course.setDurationHours(request.getDurationHours());
        course.setPrice(request.getPrice());
        course.setCreationDate(LocalDateTime.now());
        course.setStatus("DRAFT");
        
        if (instructorEmail != null) {
            User instructor = userService.findByEmail(instructorEmail)
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));
            course.setInstructor(instructor);
        }
        
        course = courseRepository.save(course);
        return mapToCourseResponse(course);
    }

    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request, String requesterEmail) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
                
        verifyCourseOwnership(course, requesterEmail);
                
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setDifficultyLevel(request.getDifficultyLevel());
        course.setDurationHours(request.getDurationHours());
        course.setPrice(request.getPrice());
        course.setLastUpdatedDate(LocalDateTime.now());
        
        course = courseRepository.save(course);
        return mapToCourseResponse(course);
    }

    @Transactional
    public void deleteCourse(Long id, String requesterEmail) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        verifyCourseOwnership(course, requesterEmail);
        
        courseRepository.delete(course);
    }

    private void verifyCourseOwnership(Course course, String requesterEmail) {
        User requester = userService.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        if (requester.getRole() == com.fahmak.alena.user.entity.Role.ADMIN) {
            return; // Admins can modify any course
        }
        
        if (course.getInstructor() == null || !course.getInstructor().getEmail().equals(requesterEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to modify this course");
        }
    }

    @Transactional
    public void enrollInCourse(Long courseId, String userEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
                
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        if (courseEnrollmentRepository.existsByStudentAndCourse(user, course)) {
            throw new RuntimeException("Already enrolled in this course");
        }
        
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(user);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setProgressPercentage(0);
        enrollment.setStatus("ACTIVE");
        
        courseEnrollmentRepository.save(enrollment);
        
        notificationService.createNotification(
                user.getId(),
                "Course Enrolled",
                "You have successfully enrolled in: " + course.getTitle(),
                NotificationType.COURSE
        );
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getMyEnrollments(String userEmail) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return courseEnrollmentRepository.findByStudent(user).stream()
                .map(enrollment -> mapToCourseResponse(enrollment.getCourse()))
                .collect(Collectors.toList());
    }

    // ── Methods exposed for cross-module consumption (InstructorService) ──

    @Transactional(readOnly = true)
    public List<Course> getInstructorCourses(User instructor) {
        return courseRepository.findByInstructor(instructor);
    }

    @Transactional(readOnly = true)
    public List<CourseEnrollment> getEnrollmentsForCourses(List<Course> courses) {
        if (courses.isEmpty()) {
            return List.of();
        }
        return courseEnrollmentRepository.findByCourseIn(courses);
    }

    private CourseResponse mapToCourseResponse(Course course) {
        String instructorName = course.getInstructor() != null ? 
                course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName() : "Unknown Instructor";

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructorName(instructorName)
                .category(course.getCategory())
                .difficultyLevel(course.getDifficultyLevel())
                .durationHours(course.getDurationHours())
                .price(course.getPrice())
                .creationDate(course.getCreationDate())
                .build();
    }
}
