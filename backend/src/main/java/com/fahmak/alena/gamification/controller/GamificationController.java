package com.fahmak.alena.gamification.controller;

import com.fahmak.alena.gamification.dto.AwardXpRequest;
import com.fahmak.alena.gamification.dto.GamificationProfileDTO;
import com.fahmak.alena.gamification.dto.LeaderboardEntryDTO;
import com.fahmak.alena.gamification.service.GamificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService gamificationService;

    @GetMapping("/users/{userId}/profile")
    public ResponseEntity<GamificationProfileDTO> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(gamificationService.getUserGamificationProfile(userId));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDTO>> getLeaderboard() {
        return ResponseEntity.ok(gamificationService.getLeaderboard());
    }

    @PostMapping("/users/{userId}/award")
    public ResponseEntity<?> awardXp(
            @PathVariable Long userId,
            @Valid @RequestBody AwardXpRequest request,
            Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        try {
            gamificationService.awardXpSecure(authentication.getName(), userId, request);
            return ResponseEntity.ok(Map.of("message", "XP awarded successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }
}
