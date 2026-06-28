package com.fahmak.alena.immersive.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PeerDTO {
    private String name;
    private String avatarUrl;
    private String level;
}
