package com.fahmak.alena.user.controller;

import com.fahmak.alena.user.dto.AuthResponse;
import com.fahmak.alena.user.dto.LoginRequest;
import com.fahmak.alena.user.dto.RegisterRequest;
import com.fahmak.alena.user.dto.OAuthLoginRequest;
import com.fahmak.alena.user.entity.Role;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.security.JwtService;
import com.fahmak.alena.user.service.OAuthService;
import com.fahmak.alena.user.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final OAuthService oauthService;


    @PostMapping("/oauth2/google")
    public ResponseEntity<?> loginWithGoogle(@Valid @RequestBody OAuthLoginRequest request, HttpServletResponse responseObj) {
        try {
            Role requestedRole = null;
            if (request.getRole() != null) {
                try {
                    requestedRole = Role.valueOf(request.getRole().toUpperCase());
                } catch (IllegalArgumentException e) {
                    // ignore invalid role
                }
            }
            AuthResponse response = oauthService.authenticateWithGoogle(request.getToken(), requestedRole);
            // Need to retrieve user to generate refresh token
            User user = userService.findByEmail(response.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
            String refreshToken = jwtService.generateRefreshToken(user);
            String accessToken = jwtService.generateToken(user);
            
            response.setAccessToken(accessToken);
            setCookies(responseObj, accessToken, refreshToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/oauth2/facebook")
    public ResponseEntity<?> loginWithFacebook(@Valid @RequestBody OAuthLoginRequest request, HttpServletResponse responseObj) {
        try {
            Role requestedRole = null;
            if (request.getRole() != null) {
                try {
                    requestedRole = Role.valueOf(request.getRole().toUpperCase());
                } catch (IllegalArgumentException e) {
                    // ignore invalid role
                }
            }
            AuthResponse response = oauthService.authenticateWithFacebook(request.getToken(), requestedRole);
            // Need to retrieve user to generate refresh token
            User user = userService.findByEmail(response.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
            String refreshToken = jwtService.generateRefreshToken(user);
            String accessToken = jwtService.generateToken(user);
            
            response.setAccessToken(accessToken);
            setCookies(responseObj, accessToken, refreshToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse responseObj) {
        try {
            User user = userService.registerUser(
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getRole()
            );

            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            setCookies(responseObj, token, refreshToken);

            AuthResponse response = AuthResponse.builder()
                    .message("User registered successfully")
                    .userId(user.getId())
                    .role(user.getRole().name())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .accessToken(token)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse responseObj) {
        java.util.Optional<User> userOptional = userService.authenticateUser(
                request.getEmail(), request.getPassword()
        );

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            setCookies(responseObj, token, refreshToken);

            AuthResponse response = AuthResponse.builder()
                    .message("Login successful")
                    .userId(user.getId())
                    .role(user.getRole().name())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .accessToken(token)
                    .build();

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse responseObj) {
        clearCookies(responseObj);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse responseObj) {
        if (refreshToken == null || !jwtService.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or missing refresh token"));
        }

        String email = jwtService.extractEmail(refreshToken);
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        
        String newToken = jwtService.generateToken(user);
        
        ResponseCookie cookie = ResponseCookie.from("accessToken", newToken)
                .httpOnly(true)
                .secure(false) // Change to true in production
                .path("/")
                .maxAge(15 * 60) // 15 minutes
                .sameSite("Lax")
                .build();
        
        responseObj.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
        AuthResponse response = AuthResponse.builder()
                .message("Token refreshed")
                .userId(user.getId())
                .role(user.getRole().name())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .accessToken(newToken)
                .build();
                
        return ResponseEntity.ok(response);
    }

    /**
     * Returns current user info from the JWT token.
     * Used by the frontend to verify authentication state on page refresh.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(name = "accessToken", required = false) String token, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (token == null && authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No token provided"));
            }
            
            String email = jwtService.extractEmail(token);
            Long userId = jwtService.extractUserId(token);
            String role = jwtService.extractRole(token);

            return ResponseEntity.ok(Map.of(
                    "userId", userId,
                    "email", email,
                    "role", role
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid token"));
        }
    }

    private void setCookies(HttpServletResponse responseObj, String accessToken, String refreshToken) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false) // Set to true in prod with HTTPS
                .path("/")
                .maxAge(15 * 60) // 15 minutes
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // Set to true in prod with HTTPS
                .path("/api/auth/refresh") // Only sent to refresh endpoint
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite("Lax")
                .build();

        responseObj.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        responseObj.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private void clearCookies(HttpServletResponse responseObj) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/auth/refresh")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        responseObj.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        responseObj.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
