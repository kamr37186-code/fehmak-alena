package com.fahmak.alena.immersive.entity;

import com.fahmak.alena.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "immersive_sessions")
@Data
@NoArgsConstructor
public class ImmersiveSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    @ToString.Exclude
    @JsonIgnore
    private User user;

    private String modelName = "Human Heart";
    private int aiScore = 0;
    private int notesGenerated = 0;
    private int currentTranscriptIndex = 0;
}
