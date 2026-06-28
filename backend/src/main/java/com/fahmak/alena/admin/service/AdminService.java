package com.fahmak.alena.admin.service;

import com.fahmak.alena.admin.dto.AdminDashboardResponse;
import com.fahmak.alena.admin.dto.SystemStatsDTO;
import com.fahmak.alena.admin.dto.UserManagementDTO;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardData() {
        List<User> users = userService.findAll();
        
        List<UserManagementDTO> userDtos = users.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        long activeUsers = users.stream()
                .filter(u -> "ACTIVE".equals(u.getStatus()))
                .count();

        SystemStatsDTO stats = SystemStatsDTO.builder()
                .concurrentUsers((int) activeUsers)
                .systemUptime(99.99)
                .monthlyRevenue("$0")
                .userGrowthTrend(0.0)
                .revenueGrowthTrend(0.0)
                .proConversion(0.0)
                .ltvAverage(0.0)
                .build();

        return AdminDashboardResponse.builder()
                .systemStats(stats)
                .users(userDtos)
                .totalUsers(users.size())
                .build();
    }

    private UserManagementDTO mapToDTO(User user) {
        return UserManagementDTO.builder()
                .id("USR-" + String.format("%04d", user.getId()))
                .name(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus() != null ? user.getStatus() : "Active")
                .lastActivity(user.getLastLoginDate() != null ? user.getLastLoginDate() : user.getRegistrationDate())
                .build();
    }
}
