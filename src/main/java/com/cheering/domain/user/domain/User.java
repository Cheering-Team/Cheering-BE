package com.cheering.domain.user.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.domain.community.domain.Community;
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
import java.net.URL;
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
public class User extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(length = 20)
    private String name;

    @Column(length = 20)
    private String nickname;

    @Column(length = 20)
    private String password;

    @Column(length = 25)
    private String email;

    private URL profileImage;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    public void connectTeam(Team team) {
        this.team = team;
        team.getPlayers().add(this);
    }
}
