package com.cheering.team;

import com.cheering.community.Community;
import com.cheering.team.league.League;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="team_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Team {
    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    @Column(length = 15, nullable = false)
    private String firstName;

    @Column(length = 15)
    private String secondName;

    @Column
    private String image;

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    @OneToOne(mappedBy = "team")
    private Community community;

    @Builder
    public Team(Long teamId, String firstName, String secondName, League league, String image) {
        this.id = teamId;
        this.firstName = firstName;
        this.secondName = secondName;
        this.league = league;
        this.image = image;
    }
}
