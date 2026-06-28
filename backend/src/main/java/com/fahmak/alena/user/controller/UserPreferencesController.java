package com.fahmak.alena.user.controller;

import com.fahmak.alena.user.dto.UserPreferencesDTO;
import com.fahmak.alena.user.service.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserPreferencesController {

    private final UserPreferencesService preferencesService;

    @GetMapping
    public ResponseEntity<UserPreferencesDTO> getPreferences() {
        return ResponseEntity.ok(preferencesService.getCurrentUserPreferences());
    }

    @PutMapping
    public ResponseEntity<UserPreferencesDTO> updatePreferences(@RequestBody UserPreferencesDTO dto) {
        return ResponseEntity.ok(preferencesService.updateCurrentUserPreferences(dto));
    }
}
