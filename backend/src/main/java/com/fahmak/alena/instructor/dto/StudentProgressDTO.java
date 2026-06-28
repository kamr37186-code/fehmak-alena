package com.fahmak.alena.instructor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentProgressDTO {
    private String id;
    private String studentName;
    private String studentEmail;
    private String enrolledCourse;
    private int progressPercentage;
    private int currentScore;
    private String avatarUrl; // We can use an external API like ui-avatars
}
