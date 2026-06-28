package com.fahmak.alena.instructor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstructorStatsDTO {
    private int totalActiveStudents;
    private double avgCourseScore;
    private double completionRate;
    private int pendingAssignments;
    private int newDiscussions;
    private int liveSession1Joined;
    private int liveSession2Joined;
}
