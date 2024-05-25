package com.cheering.team;

import com.cheering.user.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name="team_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Category category;

    @Column
    @Enumerated(value = EnumType.STRING)
    private League league;

    @Builder
    public Team(Long teamId, String name, Category category, League league) {
        this.id = teamId;
        this.name = name;
        this.category = category;
        this.league = league;
    }
}
