package com.cheering.matchRestriction;

import com.cheering.BaseTimeEntity;
import com.cheering.match.Match;
import com.cheering.match.MatchStatus;
import com.cheering.team.Team;
import com.cheering.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_restriction_tb")
@RequiredArgsConstructor
@Getter
@Setter
public class MatchRestriction extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "match_restriction_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;


    @Builder
    public MatchRestriction(User user, Match match) {
        this.user = user;
        this.match = match;
    }
}
