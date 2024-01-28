package com.cheering.user.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.community.domain.Community;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    private Community community;
}
