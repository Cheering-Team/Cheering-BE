package com.cheering.user.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.community.domain.PersonalCommunity;
import com.cheering.community.domain.Team;
import com.cheering.global.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Player extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToOne(fetch = LAZY, mappedBy = "player")
    private PersonalCommunity community;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

}
