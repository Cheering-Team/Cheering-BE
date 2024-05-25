package com.cheering.team.league;

import com.cheering.team.sport.Sport;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name="league_tb")
@NoArgsConstructor
public class League {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "sport_id")
    private Sport sport;
}
