package com.fahmak.alena.admin.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserManagementDTO {
    private String id; // prefixed with "USR-"
    private String name;
    private String email;
    private String role;
    private String status;
    private LocalDateTime lastActivity;
}
