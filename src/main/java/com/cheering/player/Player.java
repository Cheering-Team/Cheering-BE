package com.cheering.player;

import com.cheering.team.Team;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="player_tb")
@Getter
@Setter
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_seq_gen")
    @SequenceGenerator(name = "community_seq_gen", sequenceName = "community_id_sequence", allocationSize = 1)
    @Column(name = "player_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String koreanName;

    @Column(length = 100)
    private String englishName;

    @Column(length = 2000)
    private String image;

    @Column(length = 2000)
    private String backgroundImage;

    @ManyToOne
    @JoinColumn(name = "first_team_id")
    private Team firstTeam;

    @Builder
    public Player(String koreanName, String englishName, String image, String backgroundImage, Team firstTeam) {
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.image = image;
        this.backgroundImage = backgroundImage;
        this.firstTeam = firstTeam;
    }
}
