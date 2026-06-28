package com.fahmak.alena.user.service;

import com.fahmak.alena.user.entity.Role;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String email, String password, String firstName, String lastName, Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new com.fahmak.alena.user.exception.UserAlreadyExistsException("Email already in use.");
        }
        if (role == Role.ADMIN) {
            throw new IllegalArgumentException("Registration of ADMIN accounts is not allowed.");
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(email); // For now, use email as username
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setAuthProvider(com.fahmak.alena.user.entity.AuthProvider.LOCAL);
        user.setRegistrationDate(LocalDateTime.now());
        user.setStatus("ACTIVE");

        return userRepository.save(user);
    }

    @Transactional
    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getAuthProvider() != com.fahmak.alena.user.entity.AuthProvider.LOCAL) {
                throw new RuntimeException("Please login with your social account.");
            }
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                user.setLastLoginDate(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void updateProfile(User user, String firstName, String lastName) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
