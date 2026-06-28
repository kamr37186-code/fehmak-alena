package com.fahmak.alena.admin.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AdminDashboardResponse {
    private SystemStatsDTO systemStats;
    private List<UserManagementDTO> users;
    private int totalUsers;
}
