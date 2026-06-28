package com.fahmak.alena.course.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    private String title;
    private String contentUrl; // Video URL, Document link, etc.
    private String lessonType; // VIDEO, ARTICLE, INTERACTIVE
    private Integer durationMinutes;
    private Integer orderIndex;
}
