package com.cheering.community.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.user.domain.Player;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@DiscriminatorValue("player_community")
public class PlayerCommunity extends Community {
    @Id
    @GeneratedValue
    private Long id;

    private Long fanCount;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "player_id")
    private Player player;
}

