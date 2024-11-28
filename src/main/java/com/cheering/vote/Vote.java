package com.cheering.vote;

import com.cheering.BaseTimeEntity;
import com.cheering.match.Match;
import com.cheering.post.Post;
import com.cheering.vote.fanVote.FanVote;
import com.cheering.vote.voteOption.VoteOption;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vote_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Vote extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "vote_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @OneToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.REMOVE)
    private List<VoteOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "vote", cascade = CascadeType.REMOVE)
    private List<FanVote> fanVotes = new ArrayList<>();

    @Builder
    public Vote(String title, LocalDateTime endTime, Post post, Match match) {
        this.title = title;
        this.endTime = endTime;
        this.post = post;
        this.match = match;
    }
}
