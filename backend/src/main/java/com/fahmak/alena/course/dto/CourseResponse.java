package com.fahmak.alena.course.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String instructorName;
    private String category;
    private String difficultyLevel;
    private Integer durationHours;
    private Double price;
    private LocalDateTime creationDate;
}
