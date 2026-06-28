package com.fahmak.alena.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuthLoginRequest {
    @NotBlank(message = "Token is required")
    private String token;

    private String provider; // e.g. GOOGLE or FACEBOOK

    private String role; // STUDENT, INSTRUCTOR, ADMIN
}
