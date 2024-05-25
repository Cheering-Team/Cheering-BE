package com.cheering.team.relation;

import com.cheering.player.Player;
import com.cheering.team.Team;
import jakarta.persistence.*;

@Entity
@Table(name = "team_player_tb")
public class TeamPlayer {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
}
