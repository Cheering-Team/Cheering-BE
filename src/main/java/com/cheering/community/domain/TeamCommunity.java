package com.cheering.community.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("player_community")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamCommunity extends Community {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    Team team;
}
