package com.fahmak.alena.instructor.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class InstructorDashboardResponse {
    private InstructorStatsDTO stats;
    private List<StudentProgressDTO> studentProgress;
    private int totalStudents;
    private int flaggedSessionCount;
}
