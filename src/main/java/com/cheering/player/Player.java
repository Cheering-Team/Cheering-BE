package com.cheering.player;

import com.cheering.team.Team;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="player_tb")
@Getter
public class Player {
    @Id
    @GeneratedValue
    @Column(name = "player_id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String koreanName;

    @Column(length = 20, nullable = false)
    private String englishName;

    @Column
    private String image;

    @Column
    private String backgroundImage;

    @OneToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Builder
    public Player(Long playerId, String koreanName, String englishName, String image, String backgroundImage, Team team) {
        this.id = playerId;
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.image = image;
        this.backgroundImage = backgroundImage;
        this.team = team;
    }
}
