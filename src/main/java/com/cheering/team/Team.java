package com.cheering.team;

import com.cheering.team.league.League;
import com.cheering.team.relation.TeamPlayer;
import com.cheering.team.sport.Sport;
import com.cheering.user.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="team_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    @OneToMany(mappedBy = "team")
    private List<TeamPlayer> teamPlayers;

    @Builder
    public Team(Long teamId, String name) {
        this.id = teamId;
        this.name = name;
    }
}
