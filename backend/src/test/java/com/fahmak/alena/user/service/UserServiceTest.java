package com.fahmak.alena.user.service;

import com.fahmak.alena.user.entity.AuthProvider;
import com.fahmak.alena.user.entity.Role;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldSaveAndReturnUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded_pw");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("new@test.com");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser("new@test.com", "password123", "John", "Doe", Role.STUDENT);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("new@test.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists_ShouldThrowException() {
        when(userRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(RuntimeException.class, () -> 
            userService.registerUser("existing@test.com", "pw", "J", "D", Role.STUDENT)
        );

        assertEquals("Email already in use.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_Success() {
        User user = new User();
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setPasswordHash("encoded_pw");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded_pw")).thenReturn(true);

        Optional<User> result = userService.authenticateUser("test@test.com", "password123");

        assertTrue(result.isPresent());
        verify(userRepository).save(user); // lastLoginDate is updated
    }

    @Test
    void authenticateUser_WrongPassword() {
        User user = new User();
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setPasswordHash("encoded_pw");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_pw", "encoded_pw")).thenReturn(false);

        Optional<User> result = userService.authenticateUser("test@test.com", "wrong_pw");

        assertFalse(result.isPresent());
    }

    @Test
    void authenticateUser_SocialProvider_ShouldThrowException() {
        User user = new User();
        user.setAuthProvider(AuthProvider.GOOGLE);
        when(userRepository.findByEmail("social@test.com")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(RuntimeException.class, () -> 
            userService.authenticateUser("social@test.com", "pw")
        );

        assertEquals("Please login with your social account.", exception.getMessage());
    }
}
