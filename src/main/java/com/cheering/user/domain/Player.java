package com.cheering.user.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.community.domain.PlayerCommunity;
import com.cheering.community.domain.TeamCommunity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DiscriminatorValue("player")
@Getter
public class Player extends User {

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "player_community_id")
    private PlayerCommunity playerCommunity;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_community_id")
    private TeamCommunity teamCommunity;

    public void connectTeamCommunity(TeamCommunity community) {
        community.getPlayers().add(this);
        teamCommunity = community;
    }

}
