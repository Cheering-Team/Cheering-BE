package com.cheering.player.relation;

import com.cheering.player.Player;
import com.cheering.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "player_user_tb")
@RequiredArgsConstructor
@Getter
@Setter
public class PlayerUser {

    @Id
    @GeneratedValue
    @Column(name = "player_user_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "uesr_id")
    private User user;

    @Column(length = 20, nullable = false, unique = true)
    private String nickname;

    @Column
    private String image;

    @Builder
    public PlayerUser(Player player, User user, String nickname, String image) {
        this.player = player;
        this.user = user;
        this.nickname = nickname;
        this.image = image;
    }
}
