package com.cheering.player;

import com.cheering.player.relation.PlayerUser;
import com.cheering.team.Team;
import com.cheering.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="player_tb")
@Getter
@Setter
public class Player {
    @Id
    @GeneratedValue
    @Column(name = "player_id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String koreanName;

    @Column(length = 40, nullable = false)
    private String englishName;

    @Column
    private String image;

    @Column
    private String backgroundImage;

    @OneToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToOne(mappedBy = "player")
    private User user;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private PlayerUser owner;

    @Builder
    public Player(String koreanName, String englishName, String image, String backgroundImage, Team team) {
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.image = image;
        this.backgroundImage = backgroundImage;
        this.team = team;
    }
}
