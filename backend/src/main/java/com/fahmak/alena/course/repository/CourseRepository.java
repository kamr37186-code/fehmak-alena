package com.fahmak.alena.course.repository;

import com.fahmak.alena.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @EntityGraph(attributePaths = "instructor")
    List<Course> findAll();

    @EntityGraph(attributePaths = "instructor")
    List<Course> findByDifficultyLevel(String difficultyLevel);
    
    @EntityGraph(attributePaths = "instructor")
    List<Course> findTop3ByOrderByCreationDateDesc();
    
    // instructor is already fetched since we filter by it usually, but let's be explicit
    @EntityGraph(attributePaths = "instructor")
    List<Course> findByInstructor(com.fahmak.alena.user.entity.User instructor);
}
