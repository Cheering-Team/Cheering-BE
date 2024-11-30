package com.cheering.vote.voteOption;

import com.cheering.vote.Vote;
import com.cheering.vote.fanVote.FanVote;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vote_option_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class VoteOption {
    @Id
    @GeneratedValue
    @Column(name = "vote_option_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String image;

    @Column
    private String backgroundImage;

    @Column
    private Long communityId;

    @ManyToOne
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    @OneToMany(mappedBy = "voteOption", cascade = CascadeType.REMOVE)
    private List<FanVote> fanVotes = new ArrayList<>();

    @Builder
    public VoteOption(String name, String image, String backgroundImage, Long communityId, Vote vote) {
        this.name = name;
        this.image = image;
        this.backgroundImage = backgroundImage;
        this.communityId = communityId;
        this.vote = vote;
    }
}
