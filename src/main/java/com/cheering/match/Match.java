package com.cheering.match;

import com.cheering.cheer.Cheer;
import com.cheering.team.Team;
import com.cheering.vote.Vote;
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

    @Enumerated(value = EnumType.STRING)
    private MatchStatus status;

    private LocalDateTime time;

    private String location;

    private String radarId;

    private Long homeScore;

    private Long awayScore;

    private Boolean isMatchNotified;

    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    @OneToMany(mappedBy = "match", cascade = CascadeType.REMOVE)
    private List<Cheer> cheers = new ArrayList<>();

    @OneToOne(mappedBy = "match", cascade = CascadeType.REMOVE)
    private Vote vote;

    @Builder
    public Match(MatchStatus status, LocalDateTime time, String location, String radarId, Long homeScore, Long awayScore, Team homeTeam, Team awayTeam) {
        this.status = status;
        this.time = time;
        this.location = location;
        this.radarId = radarId;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }
}
