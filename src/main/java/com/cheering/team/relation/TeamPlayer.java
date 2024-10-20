package com.cheering.team.relation;

import com.cheering.community.Community;
import com.cheering.team.Team;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "team_player_tb")
@RequiredArgsConstructor
public class TeamPlayer {

    @Id
    @GeneratedValue
    @Column(name = "team_player_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Community community;

    @Builder
    public TeamPlayer(Team team, Community community) {
        this.team = team;
        this.community = community;
    }
}
