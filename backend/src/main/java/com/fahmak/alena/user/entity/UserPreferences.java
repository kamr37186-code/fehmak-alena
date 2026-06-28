package com.fahmak.alena.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    @ToString.Exclude
    @JsonIgnore
    private User user;

    private boolean highContrastMode = false;
    private boolean colorBlindnessFilters = false;
    private int textScaling = 100;
    private boolean reducedMotion = false;
    private String aiPersona = "Socratic Tutor";
}
