package com.cheering.team;

import com.cheering.team.league.League;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="team_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_seq_gen")
    @SequenceGenerator(name = "community_seq_gen", sequenceName = "community_id_sequence", allocationSize = 1)
    @Column(name = "team_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String koreanName;

    @Column(length = 100)
    private String englishName;

    @Column(length = 100)
    private String shortName;

    @Column(length = 2000)
    private String image;

    @Column(length = 2000)
    private String backgroundImage;

    @Column
    private String color;

    @Column
    private String radarId;

    @Column
    private String location;

    @ElementCollection
    @CollectionTable(name = "team_aliases", joinColumns = @JoinColumn(name = "team_id"))
    @Column(name = "alias")
    private List<String> aliases = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    @Builder
    public Team(String koreanName, String englishName, String shortName, String image, String backgroundImage, League league, String radarId, String location) {
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.shortName = shortName;
        this.image = image;
        this.backgroundImage = backgroundImage;
        this.league = league;
        this.radarId = radarId;
        this.location = location;
    }
}
