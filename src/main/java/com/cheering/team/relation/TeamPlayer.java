package com.cheering.team.relation;

import com.cheering.player.Player;
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
    private Player player;

    @Builder
    public TeamPlayer(Team team, Player player) {
        this.team = team;
        this.player = player;
    }
}
