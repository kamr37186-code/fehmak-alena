package com.fahmak.alena.immersive.service;

import com.fahmak.alena.immersive.dto.ImmersiveSessionDTO;
import com.fahmak.alena.immersive.dto.PeerDTO;
import com.fahmak.alena.immersive.entity.ImmersiveSession;
import com.fahmak.alena.immersive.repository.ImmersiveSessionRepository;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImmersiveHubService {

    private final ImmersiveSessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public ImmersiveSessionDTO getCurrentSession() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ImmersiveSession session = sessionRepository.findByUserEmail(email)
                .orElseGet(() -> createDefaultSession(email));
        return mapToDTO(session);
    }

    @Transactional
    public ImmersiveSessionDTO generateNote() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ImmersiveSession session = sessionRepository.findByUserEmail(email)
                .orElseGet(() -> createDefaultSession(email));
        
        session.setNotesGenerated(session.getNotesGenerated() + 1);
        session.setAiScore(Math.min(100, session.getAiScore() + 2)); // Increase score slightly
        return mapToDTO(sessionRepository.save(session));
    }

    public List<PeerDTO> matchPeers() {
        // Simulating finding real-time peers dynamically
        List<PeerDTO> pool = Arrays.asList(
                new PeerDTO("Omar", "https://ui-avatars.com/api/?name=Omar&background=random", "Lvl 34 Expert"),
                new PeerDTO("Salma", "https://ui-avatars.com/api/?name=Salma&background=random", "Lvl 41 Expert"),
                new PeerDTO("Youssef", "https://ui-avatars.com/api/?name=Youssef&background=random", "Lvl 29 Scholar"),
                new PeerDTO("Nour", "https://ui-avatars.com/api/?name=Nour&background=random", "Lvl 50 Master"),
                new PeerDTO("Mariam", "https://ui-avatars.com/api/?name=Mariam&background=random", "Lvl 38 Expert")
        );
        Collections.shuffle(pool);
        return pool.subList(0, 3);
    }

    private ImmersiveSession createDefaultSession(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ImmersiveSession session = new ImmersiveSession();
        session.setUser(user);
        session.setModelName("Human Heart");
        session.setAiScore(85);
        session.setNotesGenerated(0);
        session.setCurrentTranscriptIndex(0);
        return sessionRepository.save(session);
    }

    private ImmersiveSessionDTO mapToDTO(ImmersiveSession entity) {
        ImmersiveSessionDTO dto = new ImmersiveSessionDTO();
        dto.setModelName(entity.getModelName());
        dto.setAiScore(entity.getAiScore());
        dto.setNotesGenerated(entity.getNotesGenerated());
        dto.setCurrentTranscriptIndex(entity.getCurrentTranscriptIndex());
        return dto;
    }
}
