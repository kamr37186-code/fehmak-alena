package com.fahmak.alena.user.service;

import com.fahmak.alena.user.dto.UserPreferencesDTO;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.entity.UserPreferences;
import com.fahmak.alena.user.repository.UserPreferencesRepository;
import com.fahmak.alena.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPreferencesService {

    private final UserPreferencesRepository preferencesRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserPreferencesDTO getCurrentUserPreferences() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserPreferences prefs = preferencesRepository.findByUserEmail(email)
                .orElseGet(() -> createDefaultPreferences(email));
        return mapToDTO(prefs);
    }

    @Transactional
    public UserPreferencesDTO updateCurrentUserPreferences(UserPreferencesDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserPreferences prefs = preferencesRepository.findByUserEmail(email)
                .orElseGet(() -> createDefaultPreferences(email));

        prefs.setHighContrastMode(dto.isHighContrastMode());
        prefs.setColorBlindnessFilters(dto.isColorBlindnessFilters());
        prefs.setTextScaling(dto.getTextScaling());
        prefs.setReducedMotion(dto.isReducedMotion());
        prefs.setAiPersona(dto.getAiPersona());

        UserPreferences saved = preferencesRepository.save(prefs);
        return mapToDTO(saved);
    }

    private UserPreferences createDefaultPreferences(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserPreferences prefs = new UserPreferences();
        prefs.setUser(user);
        prefs.setHighContrastMode(false);
        prefs.setColorBlindnessFilters(false);
        prefs.setTextScaling(100);
        prefs.setReducedMotion(false);
        prefs.setAiPersona("Socratic Tutor");
        return preferencesRepository.save(prefs);
    }

    private UserPreferencesDTO mapToDTO(UserPreferences entity) {
        UserPreferencesDTO dto = new UserPreferencesDTO();
        dto.setHighContrastMode(entity.isHighContrastMode());
        dto.setColorBlindnessFilters(entity.isColorBlindnessFilters());
        dto.setTextScaling(entity.getTextScaling());
        dto.setReducedMotion(entity.isReducedMotion());
        dto.setAiPersona(entity.getAiPersona());
        return dto;
    }
}
