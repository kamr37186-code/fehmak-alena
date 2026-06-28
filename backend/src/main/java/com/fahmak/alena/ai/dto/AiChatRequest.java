package com.fahmak.alena.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {
    private String message;
    private String context; // Optional: course context, lesson topic, etc.
}
