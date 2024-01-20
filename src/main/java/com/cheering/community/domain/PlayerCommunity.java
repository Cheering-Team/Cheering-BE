package com.cheering.community.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.user.domain.Player;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("player_community")
@SuperBuilder
@NoArgsConstructor
@Getter
public class PlayerCommunity extends Community {
    @OneToOne(fetch = LAZY, mappedBy = "playerCommunity")
    @JoinColumn(name = "player_id")
    private Player player;

    private String image;
    private Long fanCount;
}

