package com.cheering.player.relation;

import com.cheering.player.Player;
import com.cheering.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "player_user_tb")
@RequiredArgsConstructor
public class PlayerUser {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "uesr_id")
    private User user;

    @Builder
    public PlayerUser(Player player, User user) {
        this.player = player;
        this.user = user;
    }
}
