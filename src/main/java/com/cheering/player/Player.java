package com.cheering.player;

import com.cheering.team.relation.TeamPlayer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="player_tb")
public class Player {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "player")
    private List<TeamPlayer> teamPlayers;


    @Column(length = 20, nullable = false)
    private String name;
}
