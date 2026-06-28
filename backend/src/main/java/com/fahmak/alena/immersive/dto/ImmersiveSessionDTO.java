package com.fahmak.alena.immersive.dto;

import lombok.Data;

@Data
public class ImmersiveSessionDTO {
    private String modelName;
    private int aiScore;
    private int notesGenerated;
    private int currentTranscriptIndex;
}
