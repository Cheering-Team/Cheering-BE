package com.cheering.player;

import com.cheering.team.relation.TeamPlayer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="player_tb")
@Getter
public class Player {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "player")
    private List<TeamPlayer> teamPlayers;

    @Column(length = 20, nullable = false)
    private String koreanName;

    @Column(length = 20, nullable = false)
    private String englishName;

    @Column
    private String image;

    @Column
    private String backgroundImage;

    @Column
    private Long fanCount;

    @Builder
    public Player(Long playerId, String koreanName, String englishName, String image, String backgroundImage, Long fanCount) {
        this.id = playerId;
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.image = image;
        this.backgroundImage = backgroundImage;
        this.fanCount = fanCount;
    }
}
