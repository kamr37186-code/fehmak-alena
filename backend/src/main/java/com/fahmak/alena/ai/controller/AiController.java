package com.fahmak.alena.ai.controller;

import com.fahmak.alena.ai.dto.AiChatRequest;
import com.fahmak.alena.ai.dto.AiChatResponse;
import com.fahmak.alena.ai.dto.ChatMessageDTO;
import com.fahmak.alena.ai.entity.ChatMessageEntity;
import com.fahmak.alena.ai.repository.ChatRepository;
import com.fahmak.alena.ai.service.AiService;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.entity.UserPreferences;
import com.fahmak.alena.user.repository.UserPreferencesRepository;
import com.fahmak.alena.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final UserPreferencesRepository userPreferencesRepository;

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageDTO>> getHistory(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<ChatMessageDTO> history = chatRepository.findByUserOrderByTimestampAsc(user).stream()
                .map(entity -> new ChatMessageDTO(
                        entity.getId().toString(),
                        entity.getSender(),
                        entity.getText(),
                        entity.getTimestamp()
                )).collect(Collectors.toList());

        return ResponseEntity.ok(history);
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@RequestBody AiChatRequest request, Authentication authentication) {
        String studentName = "Student";
        User user = null;
        String persona = "helpful, encouraging educational AI tutor";

        if (authentication != null && authentication.getName() != null) {
            user = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (user != null) {
                studentName = user.getFirstName() + " " + user.getLastName();
                
                // Fetch user's AI Persona preference
                UserPreferences prefs = userPreferencesRepository.findByUserEmail(user.getEmail()).orElse(null);
                if (prefs != null && prefs.getAiPersona() != null) {
                    persona = prefs.getAiPersona();
                }
            }
        }
        
        // Save user message to history
        if (user != null) {
            ChatMessageEntity userMsg = new ChatMessageEntity();
            userMsg.setUser(user);
            userMsg.setSender("user");
            userMsg.setText(request.getMessage());
            chatRepository.save(userMsg);
        }

        // Securely override the context to prevent frontend spoofing and inject the AI Persona
        String secureContext = "You are acting as a " + persona + " assisting a student named " + studentName + 
                               " in an application called Fahmak Alena. Make sure to embody the " + persona + " persona in all your responses. Keep responses educational, structured, and use markdown where appropriate.";
        request.setContext(secureContext);
        
        AiChatResponse response = aiService.getAiResponse(request);

        // Save AI message to history
        if (user != null && response != null && response.getReply() != null) {
            ChatMessageEntity aiMsg = new ChatMessageEntity();
            aiMsg.setUser(user);
            aiMsg.setSender("ai");
            aiMsg.setText(response.getReply());
            chatRepository.save(aiMsg);
        }

        return ResponseEntity.ok(response);
    }
}
