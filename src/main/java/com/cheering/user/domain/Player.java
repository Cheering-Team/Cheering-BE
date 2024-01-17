package com.cheering.user.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.community.domain.Team;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("player")
public class Player extends User {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
}
