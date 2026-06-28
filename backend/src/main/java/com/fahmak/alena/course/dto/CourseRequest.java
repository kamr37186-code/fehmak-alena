package com.fahmak.alena.course.dto;

import lombok.Data;

@Data
public class CourseRequest {
    private String title;
    private String description;
    private String category;
    private String difficultyLevel;
    private Integer durationHours;
    private Double price;
}
