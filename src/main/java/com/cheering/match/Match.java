package com.cheering.match;

import com.cheering.cheer.Cheer;
import com.cheering.team.Team;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "match_tb")
@RequiredArgsConstructor
@Getter
@Setter
public class Match {
    @Id
    @GeneratedValue
    @Column(name = "match_id")
    private Long id;
    private LocalDateTime time;
    private String location;
    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;
    @ManyToOne
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    @OneToMany(mappedBy = "match", cascade = CascadeType.REMOVE)
    private List<Cheer> cheers = new ArrayList<>();

    @Builder
    public Match(LocalDateTime time, String location, Team homeTeam, Team awayTeam) {
        this.time = time;
        this.location = location;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }
}
