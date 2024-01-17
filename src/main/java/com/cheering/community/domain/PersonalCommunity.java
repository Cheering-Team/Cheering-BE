package com.cheering.community.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.global.BaseEntity;
import com.cheering.user.domain.Player;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class PersonalCommunity extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private Long fanCount;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "player_id")
    private Player player;
}

