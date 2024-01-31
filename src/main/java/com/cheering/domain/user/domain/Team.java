package com.cheering.domain.user.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.domain.community.domain.Community;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
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
public class Team {
    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "team_community_id")
    private Community teamCommunity;

    @OneToMany(mappedBy = "team")
    private List<Player> players;
}
