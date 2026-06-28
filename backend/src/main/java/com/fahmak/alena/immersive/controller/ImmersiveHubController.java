package com.fahmak.alena.immersive.controller;

import com.fahmak.alena.immersive.dto.ImmersiveSessionDTO;
import com.fahmak.alena.immersive.dto.PeerDTO;
import com.fahmak.alena.immersive.service.ImmersiveHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/immersive-hub")
@RequiredArgsConstructor
public class ImmersiveHubController {

    private final ImmersiveHubService immersiveHubService;

    @GetMapping("/session")
    public ResponseEntity<ImmersiveSessionDTO> getSession() {
        return ResponseEntity.ok(immersiveHubService.getCurrentSession());
    }

    @PostMapping("/notes/generate")
    public ResponseEntity<ImmersiveSessionDTO> generateNote() {
        return ResponseEntity.ok(immersiveHubService.generateNote());
    }

    @PostMapping("/match-peers")
    public ResponseEntity<List<PeerDTO>> matchPeers() {
        return ResponseEntity.ok(immersiveHubService.matchPeers());
    }
}
