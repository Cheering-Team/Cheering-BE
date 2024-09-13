package com.cheering.team.sport;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="sport_tb")
@NoArgsConstructor
@Getter
public class Sport {
    @Id
    @GeneratedValue
    @Column(name = "sport_id")
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String name;

    @Builder
    public Sport(Long sportId, String name) {
        this.id = sportId;
        this.name = name;
    }
}
