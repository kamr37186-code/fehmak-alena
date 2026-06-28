package com.fahmak.alena.user.dto;

import lombok.Data;

@Data
public class UserPreferencesDTO {
    private boolean highContrastMode;
    private boolean colorBlindnessFilters;
    private int textScaling;
    private boolean reducedMotion;
    private String aiPersona;
}
