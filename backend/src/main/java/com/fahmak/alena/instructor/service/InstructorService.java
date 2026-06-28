package com.fahmak.alena.instructor.service;

import com.fahmak.alena.instructor.dto.InstructorDashboardResponse;
import com.fahmak.alena.instructor.dto.InstructorStatsDTO;
import com.fahmak.alena.instructor.dto.StudentProgressDTO;
import com.fahmak.alena.assessment.dto.IntegrityReportResponse;
import com.fahmak.alena.assessment.entity.IntegrityVerdict;
import com.fahmak.alena.assessment.repository.QuizSessionRepository;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.service.UserService;
import com.fahmak.alena.course.entity.Course;
import com.fahmak.alena.course.entity.CourseEnrollment;
import com.fahmak.alena.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final UserService userService;
    private final CourseService courseService;
    private final QuizSessionRepository quizSessionRepository;

    @Transactional(readOnly = true)
    public InstructorDashboardResponse getDashboardData(String email) {
        User instructor = null;
        if (email != null) {
            instructor = userService.findByEmail(email).orElse(null);
        }

        List<Course> instructorCourses = new ArrayList<>();
        if (instructor != null) {
            instructorCourses = courseService.getInstructorCourses(instructor);
        }

        List<CourseEnrollment> allEnrollments = courseService.getEnrollmentsForCourses(instructorCourses);

        List<StudentProgressDTO> studentDtos = allEnrollments.stream()
                .map(this::mapToProgressDTO)
                .collect(Collectors.toList());

        int totalStudents = allEnrollments.stream()
                .map(e -> e.getStudent().getId())
                .collect(Collectors.toSet())
                .size();

        double avgScore = 0.0;
        double completionRate = 0.0;
        if (!allEnrollments.isEmpty()) {
            long completed = allEnrollments.stream()
                    .filter(e -> e.getProgressPercentage() != null && e.getProgressPercentage() >= 100)
                    .count();
            completionRate = (double) completed / allEnrollments.size() * 100;

            double totalProgress = allEnrollments.stream()
                    .mapToInt(e -> e.getProgressPercentage() != null ? e.getProgressPercentage() : 0)
                    .sum();
            avgScore = totalProgress / allEnrollments.size();
        } else {
            totalStudents = 0;
            avgScore = 0.0;
            completionRate = 0.0;
        }

        InstructorStatsDTO stats = InstructorStatsDTO.builder()
                .totalActiveStudents(totalStudents)
                .avgCourseScore(Math.round(avgScore * 10.0) / 10.0)
                .completionRate(Math.round(completionRate * 10.0) / 10.0)
                .pendingAssignments(0)
                .newDiscussions(0)
                .liveSession1Joined(0)
                .liveSession2Joined(0)
                .build();

        return InstructorDashboardResponse.builder()
                .stats(stats)
                .studentProgress(studentDtos)
                .totalStudents(totalStudents)
                .flaggedSessionCount(getFlaggedSessions(email).size())
                .build();
    }

    private StudentProgressDTO mapToProgressDTO(CourseEnrollment enrollment) {
        User student = enrollment.getStudent();
        String name = student.getFirstName() + " " + student.getLastName();
        return StudentProgressDTO.builder()
                .id("STU-" + String.format("%04d", student.getId()))
                .studentName(name)
                .studentEmail(student.getEmail())
                .enrolledCourse(enrollment.getCourse().getTitle())
                .progressPercentage(enrollment.getProgressPercentage() != null ? enrollment.getProgressPercentage() : 0)
                .currentScore(enrollment.getProgressPercentage() != null ? enrollment.getProgressPercentage() : 0)
                .avatarUrl("https://ui-avatars.com/api/?name=" + name.replace(" ", "+") + "&background=random")
                .build();
    }

    @Transactional(readOnly = true)
    public List<IntegrityReportResponse> getIntegrityReports(Long quizId, String email) {
        User instructor = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        List<Long> instructorCourseIds = courseService.getInstructorCourses(instructor).stream()
                .map(Course::getId).toList();

        return quizSessionRepository.findByQuizId(quizId).stream()
                .filter(session -> session.getQuiz().getCourse() != null && instructorCourseIds.contains(session.getQuiz().getCourse().getId()))
                .map(session -> IntegrityReportResponse.builder()
                        .sessionId(session.getId())
                        .studentName(session.getUser().getFirstName() + " " + session.getUser().getLastName())
                        .quizTitle(session.getQuiz().getTitle())
                        .riskScore(session.getRiskScore())
                        .verdict(session.getIntegrityVerdict())
                        .focusLossCount(session.getTotalFocusLossCount())
                        .totalFocusLossDurationMs(session.getTotalFocusLossDurationMs())
                        .rapidAnswerCount(session.getRapidAnswerCount())
                        .copyPasteCount(session.getCopyPasteCount())
                        .events(Collections.emptyList())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IntegrityReportResponse> getFlaggedSessions(String email) {
        User instructor = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        List<Long> instructorCourseIds = courseService.getInstructorCourses(instructor).stream()
                .map(Course::getId).toList();

        return quizSessionRepository.findAll().stream()
                .filter(session -> session.getQuiz().getCourse() != null && instructorCourseIds.contains(session.getQuiz().getCourse().getId()))
                .filter(s -> s.getIntegrityVerdict() == IntegrityVerdict.FLAGGED || s.getIntegrityVerdict() == IntegrityVerdict.SUSPICIOUS)
                .map(session -> IntegrityReportResponse.builder()
                        .sessionId(session.getId())
                        .studentName(session.getUser().getFirstName() + " " + session.getUser().getLastName())
                        .quizTitle(session.getQuiz().getTitle())
                        .riskScore(session.getRiskScore())
                        .verdict(session.getIntegrityVerdict())
                        .focusLossCount(session.getTotalFocusLossCount())
                        .totalFocusLossDurationMs(session.getTotalFocusLossDurationMs())
                        .rapidAnswerCount(session.getRapidAnswerCount())
                        .copyPasteCount(session.getCopyPasteCount())
                        .events(Collections.emptyList())
                        .build())
                .collect(Collectors.toList());
    }
}
