package com.cheering.team;

import com.cheering.team.league.League;
import com.cheering.team.relation.TeamPlayer;
import com.cheering.team.sport.Sport;
import com.cheering.user.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="team_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Team {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    @Column
    private String image;

    @OneToMany(mappedBy = "team")
    private List<TeamPlayer> teamPlayers;

    @Builder
    public Team(Long teamId, String name, League league, String image) {
        this.id = teamId;
        this.name = name;
        this.league = league;
        this.image = image;
    }
}
