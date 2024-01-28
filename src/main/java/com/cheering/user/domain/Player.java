package com.cheering.user.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.community.domain.Community;
import com.cheering.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Player extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "player_id")
    private Long id;

    @Column(length = 20)
    private String name;

    @Column(length = 20)
    private String nickname;

    @Column(length = 20)
    private String password;

    @Column(length = 25)
    private String email;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "player_community_id")
    private Community community;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_community_id")
    private TeamCommunity teamCommunity;

    public void connectTeamCommunity(TeamCommunity community) {
        this.teamCommunity = community;
        community.getPlayers().add(this);
    }
}
