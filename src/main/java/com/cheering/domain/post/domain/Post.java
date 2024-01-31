package com.cheering.domain.post.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.domain.user.domain.Player;
import com.cheering.domain.user.domain.Team;
import com.cheering.domain.user.domain.User;
import com.cheering.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Post extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
}
