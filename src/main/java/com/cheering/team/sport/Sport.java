package com.cheering.team.sport;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name="sport_tb")
@NoArgsConstructor
public class Sport {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String name;
}
