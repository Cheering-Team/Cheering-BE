package com.cheering.vote.fanVote;

import com.cheering.fan.Fan;
import com.cheering.vote.Vote;
import com.cheering.vote.voteOption.VoteOption;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fan_vote_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class FanVote {
    @Id
    @GeneratedValue
    @Column(name = "fan_vote_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fan_id", nullable = false)
    private Fan fan;

    @ManyToOne
    @JoinColumn(name = "vote_option_id", nullable = false)
    private VoteOption voteOption;

    @ManyToOne
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    @Column(nullable = false)
    private boolean isVoted;
}
