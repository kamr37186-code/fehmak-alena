package com.fahmak.alena.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Typed response DTO for authentication endpoints,
 * replacing raw HashMap responses for type safety and API documentation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private Long userId;
    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String accessToken;
}
