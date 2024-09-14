package com.cheering.team.league;

import com.cheering.team.sport.Sport;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="league_tb")
@NoArgsConstructor
@Getter
public class League {
    @Id
    @GeneratedValue
    @Column(name = "league_id")
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @Builder
    public League(Long leagueId, String name, Sport sport) {
        this.id = leagueId;
        this.name = name;
        this.sport = sport;
    }
}
