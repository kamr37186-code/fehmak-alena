package com.fahmak.alena.user.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "student_profiles")
@Data
public class StudentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private String learningStyle;
    private Integer currentLevel = 1;
    private Integer totalXp = 0;
    
    @Column(columnDefinition = "TEXT")
    private String badgesEarned;
    
    @Column(columnDefinition = "TEXT")
    private String coursesCompleted;
}
