package com.fahmak.alena.assessment.entity;

import com.fahmak.alena.course.entity.Module;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private com.fahmak.alena.course.entity.Course course;

    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String difficultyLevel; // EASY, MEDIUM, HARD
    private Double passScore;
    private Integer maxAttempts;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<Question> questions;
}
